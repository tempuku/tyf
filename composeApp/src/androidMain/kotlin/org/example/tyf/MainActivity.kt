import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


//import com.example.tracker.main


@Preview
@Composable
fun DefaultPreview() {
    App()
}



@Composable
fun FileSelectorWidget(
    onFilesSelected: (List<String>) -> ManagedActivityResultLauncher<String, List<String>>
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        uris?.let { onFilesSelected(it.map{ it.toString()}) }
    }
    return launcher.launch("*/*")
}
