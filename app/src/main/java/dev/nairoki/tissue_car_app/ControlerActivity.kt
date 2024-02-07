package dev.nairoki.tissue_car_app

import android.os.Bundle
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nairoki.tissue_car_app.ui.theme.TissuecarappTheme

class ControlerActivity : ComponentActivity() {
  class WebSocketClient(val messageVal: MutableState<String>) : WebSocketListener() {
    private val ws: WebSocket

    init {
      val client = OkHttpClient()

      // 接続先のエンドポイント
      // localhostとか127.0.0.1ではないことに注意
      val request = Request.Builder()
        .url("ws://192.168.203.214:5001")
        .build()

      ws = client.newWebSocket(request, this)
    }

    fun send(message: String) {
      ws.send(message)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
      Log.d("web-sockets-debug", "WebSocket opened successfully")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
      messageVal.value=messageVal.value+"\n"+text
      //Log.d("web-sockets-debug", "Received text message: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
      webSocket.close(1000, null)
      Log.d("web-sockets-debug", "Connection closed: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
      Log.d("web-sockets-debug", "Connection failed: ${t.localizedMessage}")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val messageVal = rememberSaveable { mutableStateOf("こんにちは") }
      val webSocketClient = WebSocketClient(messageVal)
      TissuecarappTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colorScheme.background) {//modifier = Modifier.fillMaxSize(),
          Column {
            Row {
              val inputValue = rememberSaveable { mutableStateOf("") }
              TextField(
                value = inputValue.value,
                onValueChange = { inputValue.value = it },// ラムダ式の引数はitで受け取れる
                label = { "あああああ" },
                modifier = Modifier
                  .padding(16.dp)
                  .height(100.dp)
              )
              Button(onClick = {
                webSocketClient.send(inputValue.value)
                inputValue.value =""
              }) {
                Text("送る")
              }
            }
            Text(messageVal.value)
          }



        }
      }
    }
  }


}