package sample

import glmatrix.mat4
import glmatrix.toRad
import glmatrix.vec3
import org.khronos.webgl.*
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.window
import kotlin.math.PI


actual class Sample {
  actual fun checkMe() = 12
}

actual object Platform {
  actual val name: String = "JS"
}

val vertexShaderText = """
  precision mediump float;

  attribute vec3 vertPosition;
  attribute vec3 vertColor;
  varying vec3 fragColor;
  uniform mat4 mWorld;
  uniform mat4 mView;
  uniform mat4 mProj;

  void main() {
     fragColor = vertColor;
     gl_Position = mProj * mView * mWorld * vec4(vertPosition, 1.0);
  }
""".trimIndent()

val fragmentShaderText = """
    precision mediump float;

    varying vec3 fragColor;
    void main() {
      gl_FragColor = vec4(fragColor, 1.0);
    }
""".trimIndent()

typealias GL = WebGLRenderingContext

fun main(args: Array<String>) {
  val document = window.document

  val canvas = document.getElementById("gl") as HTMLCanvasElement
  val message = document.getElementById("message")!!

  try {
    val gl = canvas.getContext("webgl")
      ?: canvas.getContext("experimental-webgl")
      ?: canvas.getContext("moz-webgl")
      ?: canvas.getContext("webkit-3d")

    if (gl != null) (gl as WebGLRenderingContext).main()
    else throw Error("Your browser doesn't support WebGL.")

    message.remove()
  } catch (t: Throwable) {
    message.textContent = "ERROR: ${t.message}. See details in dev console log."
    console.error(t.asDynamic().stack)
  }
}

private fun WebGLRenderingContext.main() {
  viewport(0, 0, drawingBufferWidth, drawingBufferHeight)

  // Shaders program
  val program = createProgram(vertexShaderText, fragmentShaderText)

  // Buffers
  val boxIndices = createBox(program)

  clearColor(1f, 1f, 1f, 1f)
  clear(GL.COLOR_BUFFER_BIT)
  enable(GL.DEPTH_TEST)
  enable(GL.CULL_FACE)
  frontFace(GL.CCW)
  cullFace(GL.BACK)

  useProgram(program)

  val matWorldUniformLocation = getUniformLocation(program, "mWorld")
  val matViewUniformLocation = getUniformLocation(program, "mView")
  val matProjUniformLocation = getUniformLocation(program, "mProj")

  val worldMatrix = mat4.create()
  val viewMatrix = mat4.create()
  val projMatrix = mat4.create()

  mat4.identity(worldMatrix)
  mat4.lookAt(viewMatrix, vec3(0, 0, -8), vec3(0, 0, 0), vec3(0, 1, 0))
  mat4.perspective(projMatrix, 45f.toRad(), drawingBufferWidth.toFloat() / drawingBufferHeight, 0.1f, 1000.0f)

  uniformMatrix4fv(matWorldUniformLocation, false, worldMatrix)
  uniformMatrix4fv(matViewUniformLocation, false, viewMatrix)
  uniformMatrix4fv(matProjUniformLocation, false, projMatrix)

  val xRotationMatrix = mat4.create()
  val yRotationMatrix = mat4.create()

  // Render loop

  val identityMatrix = mat4.create()
  mat4.identity(identityMatrix)

  var angle: Float

  animationLoop { t ->
    angle = t.toFloat() / 1000 / 6 * 2 * PI.toFloat()

    mat4.rotate(yRotationMatrix, identityMatrix, angle, vec3(0, 1, 0))
    mat4.rotate(xRotationMatrix, identityMatrix, angle / 4, vec3(1, 0, 0))
    mat4.multiply(worldMatrix, yRotationMatrix, xRotationMatrix)
    uniformMatrix4fv(matWorldUniformLocation, false, worldMatrix)

    clearColor(1f, 1f, 1f, 1f)
    clear(GL.DEPTH_BUFFER_BIT)

    drawElements(GL.TRIANGLES, boxIndices.size, GL.UNSIGNED_SHORT, 0)
  }
}


private fun WebGLRenderingContext.createBox(program: WebGLProgram?): Array<Short> {
  val boxVertices = arrayOf(
//top
    -1.0, 1.0, -1.0, 0.5, 0.5, 0.5,
    -1.0, 1.0, 1.0, 0.5, 0.5, 0.5,
    1.0, 1.0, 1.0, 0.5, 0.5, 0.5,
    1.0, 1.0, -1.0, 0.5, 0.5, 0.5,
//left
    -1.0, 1.0, 1.0, 0.75, 0.25, 0.5,
    -1.0, -1.0, 1.0, 0.75, 0.25, 0.5,
    -1.0, -1.0, -1.0, 0.75, 0.25, 0.5,
    -1.0, 1.0, -1.0, 0.75, 0.25, 0.5,
//right
    1.0, 1.0, 1.0, 0.25, 0.25, 0.75,
    1.0, -1.0, 1.0, 0.25, 0.25, 0.75,
    1.0, -1.0, -1.0, 0.25, 0.25, 0.75,
    1.0, 1.0, -1.0, 0.25, 0.25, 0.75,
//front
    1.0, 1.0, 1.0, 1.0, 0.0, 0.15,
    1.0, -1.0, 1.0, 1.0, 0.0, 0.15,
    -1.0, -1.0, 1.0, 1.0, 0.0, 0.15,
    -1.0, 1.0, 1.0, 1.0, 0.0, 0.15,
//back
    1.0, 1.0, -1.0, 0.0, 1.0, 0.15,
    1.0, -1.0, -1.0, 0.0, 1.0, 0.15,
    -1.0, -1.0, -1.0, 0.0, 1.0, 0.15,
    -1.0, 1.0, -1.0, 0.0, 1.0, 0.15,
//bottom
    -1.0, -1.0, -1.0, 0.5, 0.5, 1.0,
    -1.0, -1.0, 1.0, 0.5, 0.5, 1.0,
    1.0, -1.0, 1.0, 0.5, 0.5, 1.0,
    1.0, -1.0, -1.0, 0.5, 0.5, 1.0
  ).map { it.toFloat() }.toTypedArray()

  val boxIndices = arrayOf<Short>(
//top
    0, 1, 2,
    0, 2, 3,
//left
    5, 4, 6,
    6, 4, 7,
// right
    8, 9, 10,
    8, 10, 11,
//front
    13, 12, 14,
    15, 14, 12,
//back
    16, 17, 18,
    16, 18, 19,
//bottom
    21, 20, 22,
    22, 20, 23
  )

  val boxVertexBufferObject = createBuffer()
  bindBuffer(GL.ARRAY_BUFFER, boxVertexBufferObject)
  bufferData(GL.ARRAY_BUFFER, Float32Array(boxVertices), GL.STATIC_DRAW)

  val boxIndexBufferObject = createBuffer()
  bindBuffer(GL.ELEMENT_ARRAY_BUFFER, boxIndexBufferObject)
  bufferData(GL.ELEMENT_ARRAY_BUFFER, Uint16Array(boxIndices), GL.STATIC_DRAW)

  val positionAttribLocation = getAttribLocation(program, "vertPosition")
  val colorAttribLocation = getAttribLocation(program, "vertColor")

  vertexAttribPointer(
    positionAttribLocation,
    3,
    GL.FLOAT,
    false,
    6 * Float32Array.BYTES_PER_ELEMENT,
    0
  )
  vertexAttribPointer(
    colorAttribLocation,
    3,
    GL.FLOAT,
    false,
    6 * Float32Array.BYTES_PER_ELEMENT,
    3 * Float32Array.BYTES_PER_ELEMENT
  )
  enableVertexAttribArray(positionAttribLocation)
  enableVertexAttribArray(colorAttribLocation)
  return boxIndices
}

private fun animationLoop(frame: (t: Double) -> Unit) {
  fun decoratedFrame(t: Double) {
    frame(t)
    window.requestAnimationFrame(::decoratedFrame)
  }

  window.requestAnimationFrame(::decoratedFrame)
}

private fun WebGLRenderingContext.createProgram(
  vertexShaderSource: String,
  fragmentShaderSource: String
): WebGLProgram? {
  val vertexShader = createShader(GL.VERTEX_SHADER, vertexShaderSource)
  val fragmentShader = createShader(GL.FRAGMENT_SHADER, fragmentShaderSource)

  val program = createProgram()
  attachShader(program, vertexShader)
  attachShader(program, fragmentShader)

  linkProgram(program)
  check(getProgramParameter(program, GL.LINK_STATUS) == true) {
    "Cannot link program: ${getProgramInfoLog(program)}"
  }

  validateProgram(program)
  check(getProgramParameter(program, GL.VALIDATE_STATUS) == true) {
    "Program is invalid: ${getProgramInfoLog(program)}"
  }

  return program
}

fun WebGLRenderingContext.createShader(type: Int, text: String): WebGLShader {
  val shader = createShader(type)!!

  shaderSource(shader, text)
  compileShader(shader)

  check(getShaderParameter(shader, GL.COMPILE_STATUS) == true) {
    "Cannot compile vertex shader: ${getShaderInfoLog(shader)}"
  }

  return shader
}