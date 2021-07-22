/*
 * Copyright (c) 2017-present Robert Jaros
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.kvision.remote

import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import org.w3c.dom.get
import org.w3c.dom.url.URLSearchParams
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.Promise
import kotlin.js.JSON as NativeJSON

/**
 * HTTP status unauthorized (401).
 */
const val HTTP_UNAUTHORIZED = 401

/**
 * HTTP response body types.
 */
enum class ResponseBodyType {
    JSON,
    TEXT,
    READABLE_STREAM
}

/**
 * An agent responsible for remote calls.
 */
open class CallAgent {

    private val kvUrlPrefix = window["kv_remote_url_prefix"]
    private val urlPrefix: String = if (kvUrlPrefix != undefined) "$kvUrlPrefix/" else ""
    private var counter = 1

    /**
     * Makes an JSON-RPC call to the remote server.
     * @param url an URL address
     * @param data data to be sent
     * @param method a HTTP method
     * @return a promise of the result
     */
    @Suppress("UnsafeCastFromDynamic", "ComplexMethod")
    fun jsonRpcCall(
        url: String,
        data: List<String?> = listOf(),
        method: HttpMethod = HttpMethod.POST,
        beforeSend: (() -> RequestInit)? = null
    ): Promise<String> {
        val requestInit = beforeSend?.invoke() ?: RequestInit()
        requestInit.method = method.name
        requestInit.credentials = RequestCredentials.INCLUDE
        val jsonRpcRequest = JsonRpcRequest(counter++, url, data)
        val urlAddr = urlPrefix + url.drop(1)
        val fetchUrl = if (method == HttpMethod.GET) {
            urlAddr + "?" + URLSearchParams(obj { id = jsonRpcRequest.id }).toString()
        } else {
            requestInit.body = JSON.plain.encodeToString(jsonRpcRequest)
            urlAddr
        }
        requestInit.headers = js("{}")
        requestInit.headers["Content-Type"] = "application/json"
        requestInit.headers["X-Requested-With"] = "XMLHttpRequest"
        return Promise { resolve, reject ->
            window.fetch(fetchUrl, requestInit).then { response ->
                if (response.ok) {
                    response.json().then { data: dynamic ->
                        when {
                            data.id != jsonRpcRequest.id -> reject(Exception("Invalid response ID"))
                            data.error != null -> {
                                if (data.exceptionType == "io.kvision.remote.ServiceException") {
                                    reject(ServiceException(data.error.toString()))
                                } else {
                                    reject(Exception(data.error.toString()))
                                }
                            }
                            data.result != null -> resolve(data.result)
                            else -> reject(Exception("Invalid response"))
                        }
                    }
                } else {
                    if (response.status.toInt() == HTTP_UNAUTHORIZED) {
                        reject(SecurityException(response.statusText))
                    } else {
                        reject(Exception(response.statusText))
                    }
                }
            }.catch {
                reject(Exception(it.message))
            }
        }
    }

    /**
     * Makes a remote call to the remote server.
     * @param url an URL address
     * @param data data to be sent
     * @param method a HTTP method
     * @param contentType a content type of the request
     * @param beforeSend a function to set request parameters
     * @return a promise of the result
     */
    @Suppress("UnsafeCastFromDynamic", "ComplexMethod")
    fun remoteCall(
        url: String,
        data: dynamic = null,
        method: HttpMethod = HttpMethod.GET,
        contentType: String = "application/json",
        responseBodyType: ResponseBodyType = ResponseBodyType.JSON,
        beforeSend: (() -> RequestInit)? = null
    ): Promise<dynamic> {
        val requestInit = beforeSend?.invoke() ?: RequestInit()
        requestInit.method = method.name
        requestInit.credentials = RequestCredentials.INCLUDE
        val urlAddr = urlPrefix + url.drop(1)
        val fetchUrl = if (method == HttpMethod.GET) {
            urlAddr + "?" + URLSearchParams(data).toString()
        } else {
            requestInit.body = when (contentType) {
                "application/json" -> if (data is String) data else NativeJSON.stringify(data)
                "application/x-www-form-urlencoded" -> URLSearchParams(data).toString()
                else -> data
            }
            urlAddr
        }
        requestInit.headers = js("{}")
        requestInit.headers["Content-Type"] = contentType
        requestInit.headers["X-Requested-With"] = "XMLHttpRequest"
        return Promise { resolve, reject ->
            window.fetch(fetchUrl, requestInit).then { response ->
                if (response.ok) {
                    when (responseBodyType) {
                        ResponseBodyType.JSON -> response.json().then { resolve(it) }
                        ResponseBodyType.TEXT -> response.text().then { resolve(it) }
                        ResponseBodyType.READABLE_STREAM -> resolve(response.body)
                    }
                } else {
                    if (response.status.toInt() == HTTP_UNAUTHORIZED) {
                        reject(SecurityException(response.statusText))
                    } else {
                        reject(Exception(response.statusText))
                    }
                }
            }.catch {
                reject(Exception(it.message))
            }
        }
    }
}
