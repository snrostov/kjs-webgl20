package glmatrix

import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

external class mat3 : Float32Array {
  companion object {
    fun create(): mat3
    fun normalFromMat4(out: mat3, a: mat4)
  }
}

external class vec2 : Float32Array {
  companion object {
    fun create(): vec2
    fun fromValues(x: Float, y: Float): vec2
    fun sub(out: vec2, a: vec2, b: vec2)
  }
}

fun vec2(x: Float, y: Float): vec2 {
  return vec2.fromValues(x, y)
}

infix fun vec2.midPoint(v: vec2): vec2 {
  return vec2((this[0] + v[0]) / 2f, (this[1] + v[1]) / 2f)
}

operator fun vec2.minus(other: vec2): vec2 {
  val out = vec2.create()
  vec2.sub(out, this, other); return out
}

fun Float32Array.toFloatArray(): Array<Float> {
  return Array(this.length, { i -> this[i] })
}

fun clamp(value: Float, low: Float, high: Float): Float {
  return max(low, min(value, high))
}

external class mat4 : Float32Array {
  companion object {
    fun add(out: mat4, a: mat4, b: mat4): mat4
    fun adjoint(out: mat4, a: mat4): mat4
    fun clone(a: mat4): mat4
    fun copy(out: mat4, a: mat4): mat4
    fun create(): mat4
    fun identity(out: mat4): mat4
    fun lookAt(out: mat4, eye: vec3, center: vec3, up: vec3): mat4
    fun multiply(out: mat4, a: mat4, b: mat4): mat4
    fun multiplyScalar(out: mat4, a: mat4, b: Float): mat4
    fun ortho(out: mat4, left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): mat4
    fun perspective(out: mat4, fovy: Float, aspect: Float, near: Float, far: Float): mat4
    fun rotate(out: mat4, a: mat4, rad: Float, axis: vec3): mat4
    fun rotateX(out: mat4, a: mat4, rad: Float)
    fun rotateY(out: mat4, a: mat4, rad: Float)
    fun rotateZ(out: mat4, a: mat4, rad: Float)
    fun scale(out: mat4, a: mat4, v: vec3): mat4
    fun str(a: mat4): String
    fun subtract(out: mat4, a: mat4, b: mat4): mat4
    fun targetTo(out: mat4, eye: vec3, center: vec3, up: vec3): mat4
    fun translate(out: mat4, a: mat4, v: vec3): mat4
    fun transpose(out: mat4, a: mat4): mat4
  }
}

external class vec3 : Float32Array {

  companion object {
    fun create(): vec3
    fun normalize(out: vec3, a: vec3): vec3
    fun fromValues(x: Float, y: Float, z: Float): vec3
    fun cross(out: vec3, a: vec3, b: vec3)
    fun dot(a: vec3, b: vec3): Float
    fun angle(a: vec3, b: vec3): Float
    fun sub(out: vec3, a: vec3, b: vec3)
    fun add(out: vec3, a: vec3, b: vec3)
  }
}

fun vec3(x: Float, y: Float, z: Float): vec3 {
  return vec3.fromValues(x, y, z)
}

fun vec3(x: Int, y: Int, z: Int): vec3 {
  return vec3.fromValues(x.toFloat(), y.toFloat(), z.toFloat())
}

fun vec3.set(x: Float, y: Float, z: Float): vec3 {
  this[0] = x; this[1] = y; this[2] = z; return this
}

fun vec3.setXYZ(v: Float): vec3 {
  this[0] = v; this[1] = v; this[2] = v; return this
}

fun vec3.normalize(): vec3 {
  val out = vec3.create()
  vec3.normalize(out, this); return out
}

operator fun vec3.plus(other: vec3): vec3 {
  val out = vec3.create()
  vec3.add(out, this, other); return out
}

operator fun vec3.minus(other: vec3): vec3 {
  val out = vec3.create()
  vec3.sub(out, this, other); return out
}

infix fun vec3.dot(other: vec3): Float {
  return vec3.dot(this, other)
}

infix fun vec3.midPoint(v: vec3): vec3 {
  return vec3((this[0] + v[0]) / 2f, (this[1] + v[1]) / 2f, (this[2] + v[2]) / 2f)
}

infix fun vec3.cross(other: vec3): vec3 {
  val out = vec3.create()
  vec3.cross(out, this, other); return out
}

fun <T : Float32Array> Array<T>.toFloatArray(): Array<Float> {
  return this.asIterable().toFloatArray()
}

fun <T : Float32Array> Iterable<T>.toFloatArray(): Array<Float> {
  return this.flatMap { it.toFloatArray().asIterable() }.toTypedArray()
}

//Generic math

fun Float.clamp(min: Float, max: Float): Float {
  return min(max(min, this), max)
}

fun Float.toDeg(): Float {
  return 180f * this / PI.toFloat()
}

fun Float.toRad(): Float {
  return PI.toFloat() * this / 180f
}