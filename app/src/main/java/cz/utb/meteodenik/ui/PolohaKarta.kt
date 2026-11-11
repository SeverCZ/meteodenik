
package cz.utb.meteodenik.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cz.utb.meteodenik.R
import android.content.Context
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.res.colorResource

private enum class StavPolohy {
    NEZNAMY, ODMITNUTO, TRVALE_ODMITNUTO, UDELENO
}

private fun maOpravneni(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun PolohaKarta() {
    val context = LocalContext.current
    val permission = Manifest.permission.ACCESS_COARSE_LOCATION

    val preferences = remember(context) {
        context.getSharedPreferences(
            "meteodenik_opravneni",
            Context.MODE_PRIVATE
        )
    }

    var pozadano by remember {
        mutableStateOf(
            preferences.getBoolean("poloha_pozadano", false)
        )
    }
    var stav by remember {
        mutableStateOf(
            if (maOpravneni(context)) {
                StavPolohy.UDELENO
            } else {
                StavPolohy.NEZNAMY
            }
        )
    }
    var vysvetleni by remember {
        mutableStateOf(false)
    }
    var souradnice by remember {
        mutableStateOf<String?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { povoleno ->

        pozadano = true

        preferences.edit()
            .putBoolean("poloha_pozadano", true)
            .apply()

        stav = if (povoleno) {
            StavPolohy.UDELENO
        } else {
            val activity = context as? Activity

            if (activity != null &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            ) {
                StavPolohy.ODMITNUTO
            } else {
                StavPolohy.TRVALE_ODMITNUTO
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                stav = if (maOpravneni(context)) {
                    StavPolohy.UDELENO
                } else if (!pozadano) {
                    StavPolohy.NEZNAMY
                } else {
                    val activity = context as? Activity

                    if (activity != null &&
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            permission
                        )
                    ) {
                        StavPolohy.ODMITNUTO
                    } else {
                        StavPolohy.TRVALE_ODMITNUTO
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (stav) {
                StavPolohy.NEZNAMY -> {
                    Button(onClick = {
                        launcher.launch(permission)
                    }) {
                        Text(stringResource(R.string.poloha_zjistit))
                    }
                }

                StavPolohy.ODMITNUTO -> {
                    Text(stringResource(R.string.poloha_odmitnuta))
                    Button(onClick = {
                        vysvetleni = true
                    }) {
                        Text(stringResource(R.string.poloha_znovu))
                    }
                }

                StavPolohy.TRVALE_ODMITNUTO -> {
                    Text(stringResource(R.string.poloha_natrvalo))
                    Button(onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }) {
                        Text(stringResource(R.string.otevrit_nastaveni))
                    }
                }

                StavPolohy.UDELENO -> {
                    Text(stringResource(R.string.poloha_povolena))
                    Text(
                        text = stringResource(R.string.poloha_vysvetleni),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context, permission
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            try {
                                val manager = context.getSystemService(
                                    Activity.LOCATION_SERVICE
                                ) as LocationManager

                                val location = manager.getLastKnownLocation(
                                    LocationManager.NETWORK_PROVIDER
                                ) ?: manager.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER
                                )
                                souradnice = location?.let {
                                    "${it.latitude}, ${it.longitude}"
                                }
                            } catch (e: SecurityException) {
                                stav = StavPolohy.NEZNAMY
                            } catch (e: IllegalArgumentException) {
                                souradnice = null
                            }
                        } else {
                            stav = StavPolohy.NEZNAMY
                        }
                    }) {
                        Text(stringResource(R.string.nacist_polohu))
                    }

                    Text(
                        souradnice?.let {
                            stringResource(R.string.souradnice, it)
                        } ?: stringResource(R.string.poloha_nedostupna)
                    )
                }
            }
        }
    }

    if (vysvetleni) {
        AlertDialog(
            onDismissRequest = { vysvetleni = false },
            title = {
                Text(stringResource(R.string.poloha_titulek))
            },
            text = {
                Text(stringResource(R.string.poloha_vysvetleni))
            },
            confirmButton = {
                TextButton(onClick = {
                    vysvetleni = false
                    launcher.launch(permission)
                }) {
                    Text(stringResource(R.string.poloha_povolit))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    vysvetleni = false
                }) {
                    Text(stringResource(R.string.zrusit))
                }
            }
        )
    }
}
