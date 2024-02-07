package dev.nairoki.tissue_car_app

import kotlin.concurrent.thread
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dev.nairoki.tissue_car_app.ui.theme.TissuecarappTheme
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText


class ServerTest : ComponentActivity() {
  class Server {
    fun start() {
      embeddedServer(Netty, port = 8000) {
        install(WebSockets)
        routing {
          webSocket("/echo") {
            Frame.Text("Please enter your name")
            for (frame in incoming) {
              frame as? Frame.Text ?: continue
              val receivedText = frame.readText()
              if (receivedText.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
              } else {
                send(Frame.Text("Hi, $receivedText!"))
              }
            }
          }
        }
      }.start(wait = true)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val thread = Thread {
      Server().start()
    }
    thread.start()

    setContent {
      TissuecarappTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Text("ServerTest")
        }
      }
    }
  }
}


