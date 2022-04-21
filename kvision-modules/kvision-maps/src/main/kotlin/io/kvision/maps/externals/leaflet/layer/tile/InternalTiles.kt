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

package io.kvision.maps.externals.leaflet.layer.tile

// I'm not sure why this file was here. I can't find a reference to it in
// the Leaflet docs. I think maybe it came from the @types/leaflet package.
// Maybe it's no longer needed?
// TODO resolve purpose of InternalTiles. Re-enable if it's still useful, otherwise, delete


//import io.kvision.maps.externals.leaflet.geo.Coords
//import io.kvision.maps.externals.leaflet.layer.Layer
//import kotlin.js.Date
//import org.w3c.dom.HTMLElement
//
//@JsModule("leaflet")
//@JsNonModule
//external interface InternalTiles {
//    var active: Boolean?
//    var coords: Coords
//    var current: Boolean?
//    var el: HTMLElement
//    var loaded: Date?
//    var retain: Boolean?
//}
//
///** Native getter for [InternalTiles] */
//inline operator fun InternalTiles.get(name: String): Layer<*>? =
//    asDynamic()[name] as Layer<*>?
//
///** Native setter for [InternalTiles] */
//inline operator fun InternalTiles.set(name: String, value: Layer<*>) {
//    asDynamic()[name] = value
//}
