package cz.utb.meteodenik

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.utb.meteodenik.ui.theme.MeteodenikTheme
import android.util.Log
import androidx.core.content.ContextCompat
import cz.utb.meteodenik.ui.SeznamMestObrazovka

private const val TAG = "Cyklus"

class MainActivity : ComponentActivity() {

    private val receiver = ZaznamUlozenReceiver { mesto ->
        Log.d(TAG, "MainActivity dostala zprávu o uložení: $mesto")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity  onCreate   (savedInstanceState = $savedInstanceState)")
        enableEdgeToEdge()
        setContent {
            MeteodenikTheme {
                SeznamMestObrazovka()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity  onStart")
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(ZaznamUlozenReceiver.ACTION_ZAZNAM_ULOZEN),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity  onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity  onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity  onStop")
        unregisterReceiver(receiver)
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "MainActivity  onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity  onDestroy")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Ahoj $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeteodenikTheme {
        Greeting("Android")
    }
}