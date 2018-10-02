package sample

import glmatrix.mat4
import glmatrix.toRad
import glmatrix.vec3
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.WebGLRenderingContext
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
  val gl = canvas.getContext("webgl") ?: canvas.getContext("experimental-webgl") ?: canvas.getContext("moz-webgl")
  ?: canvas.getContext("webkit-3d")

  if (gl != null) {
    gl as WebGLRenderingContext

    val extensions = gl.getSupportedExtensions()

    console.log(gl)
    console.log(extensions)

    gl.viewport(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)

    gl.clearColor(1f, 1f, 1f, 1f)
    gl.clear(GL.COLOR_BUFFER_BIT)
    gl.enable(GL.DEPTH_TEST)
    gl.enable(GL.CULL_FACE)
    gl.frontFace(GL.CCW)
    gl.cullFace(GL.BACK)

//Shaders
    val vertexShader = gl.createShader(GL.VERTEX_SHADER)
    val fragmentShader = gl.createShader(GL.FRAGMENT_SHADER)

    gl.shaderSource(vertexShader, vertexShaderText)
    gl.shaderSource(fragmentShader, fragmentShaderText)

    gl.compileShader(vertexShader)
    if (gl.getShaderParameter(vertexShader, GL.COMPILE_STATUS) != true) {
      console.error("ERROR compiling vertex shader!", gl.getShaderInfoLog(vertexShader))
      return
    }
    gl.compileShader(fragmentShader)
    if (gl.getShaderParameter(fragmentShader, GL.COMPILE_STATUS) != true) {
      console.error("ERROR compiling fragment shader!", gl.getShaderInfoLog(fragmentShader))
      return
    }

    val program = gl.createProgram()
    gl.attachShader(program, vertexShader)
    gl.attachShader(program, fragmentShader)
    gl.linkProgram(program)
    if (gl.getProgramParameter(program, GL.LINK_STATUS) == null) {
      console.error("ERROR linking program!", gl.getProgramInfoLog(program))
      return
    }
    gl.validateProgram(program)
    if (gl.getProgramParameter(program, GL.VALIDATE_STATUS) == null) {
      console.error("ERROR validating program!", gl.getProgramInfoLog(program))
      return
    }

//Buffers

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

    val boxIndices = arrayOf(
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
    ).map { it.toShort() }.toTypedArray()

    val boxVertexBufferObject = gl.createBuffer()
    gl.bindBuffer(GL.ARRAY_BUFFER, boxVertexBufferObject)
    gl.bufferData(GL.ARRAY_BUFFER, Float32Array(boxVertices), GL.STATIC_DRAW)

    val boxIndexBufferObject = gl.createBuffer()
    gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, boxIndexBufferObject)
    gl.bufferData(GL.ELEMENT_ARRAY_BUFFER, Uint16Array(boxIndices), GL.STATIC_DRAW)

    val positionAttribLocation = gl.getAttribLocation(program, "vertPosition")
    val colorAttribLocation = gl.getAttribLocation(program, "vertColor")

    gl.vertexAttribPointer(
      positionAttribLocation,
      3,
      GL.FLOAT,
      false,
      6 * Float32Array.BYTES_PER_ELEMENT,
      0
    )
    gl.vertexAttribPointer(
      colorAttribLocation,
      3,
      GL.FLOAT,
      false,
      6 * Float32Array.BYTES_PER_ELEMENT,
      3 * Float32Array.BYTES_PER_ELEMENT
    )
    gl.enableVertexAttribArray(positionAttribLocation)
    gl.enableVertexAttribArray(colorAttribLocation)

//Program used
    gl.useProgram(program)

    val matWorldUniformLocation = gl.getUniformLocation(program, "mWorld")
    val matViewUniformLocation = gl.getUniformLocation(program, "mView")
    val matProjUniformLocation = gl.getUniformLocation(program, "mProj")

    val worldMatrix = mat4.create()
    val viewMatrix = mat4.create()
    val projMatrix = mat4.create()

    mat4.identity(worldMatrix)
    mat4.lookAt(viewMatrix, vec3(0, 0, -8), vec3(0, 0, 0), vec3(0, 1, 0))
    mat4.perspective(projMatrix, 45f.toRad(), canvas.width.toFloat() / canvas.height, 0.1f, 1000.0f)

    gl.uniformMatrix4fv(matWorldUniformLocation, false, worldMatrix)
    gl.uniformMatrix4fv(matViewUniformLocation, false, viewMatrix)
    gl.uniformMatrix4fv(matProjUniformLocation, false, projMatrix)

    val xRotationMatrix = mat4.create()
    val yRotationMatrix = mat4.create()

//Render loop

    val identityMatrix = mat4.create()
    mat4.identity(identityMatrix)

    var angle: Float

    fun loop(dt: Double) {
      angle = window.performance.now().toFloat() / 1000 / 6 * 2 * PI.toFloat()
      mat4.rotate(yRotationMatrix, identityMatrix, angle, vec3(0, 1, 0))
      mat4.rotate(xRotationMatrix, identityMatrix, angle / 4, vec3(1, 0, 0))
      mat4.multiply(worldMatrix, yRotationMatrix, xRotationMatrix)
      gl.uniformMatrix4fv(matWorldUniformLocation, false, worldMatrix)

      gl.clearColor(1f, 1f, 1f, 1f)
      gl.clear(GL.DEPTH_BUFFER_BIT)

      gl.drawElements(GL.TRIANGLES, boxIndices.size, GL.UNSIGNED_SHORT, 0)

      window.requestAnimationFrame(::loop)
    }

    window.requestAnimationFrame(::loop)
  } else {
    document.write("Your browser doesn't support WebGL.")
  }
}