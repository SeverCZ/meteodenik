package cz.utb.meteodenik.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.R
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import cz.utb.meteodenik.ui.theme.MeteodenikTheme

private const val TAG = "Cyklus"
@Composable
fun DetailObrazovka(mesto: String, teplota: Double) {
    Scaffold { insety ->
        Column(modifier = Modifier.padding(insety).padding(16.dp)) {
            Text(text = mesto, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = if (teplota.isNaN()) {
                    stringResource(R.string.teplota_chybi)
                } else {
                    stringResource(R.string.detail_souhrn, mesto, teplota)
                }
            )
            SdiletTlacitko(mesto = mesto, teplota = teplota)
        }
    }
}
@Composable
fun SdiletTlacitko(mesto: String, teplota: Double) {
    val context = LocalContext.current
    val text = stringResource(R.string.sdileny_text, mesto, teplota)

    Button(onClick = {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try {
            context.startActivity(Intent.createChooser(sendIntent, null))
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Na tomto zařízení není nic, co by umělo přijmout text", e)
        }
    }) {
        Text(stringResource(R.string.sdilet_zaznam))
    }
}
@Preview(
    name = "Světlý režim",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DetailSvetlyPreview() {
    MeteodenikTheme(darkTheme = false) {
        DetailObrazovka(
            mesto = "Zlín",
            teplota = 21.4
        )
    }
}

@Preview(
    name = "Tmavý režim",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DetailTmavyPreview() {
    MeteodenikTheme(darkTheme = true) {
        DetailObrazovka(
            mesto = "Zlín",
            teplota = 21.4
        )
    }
}