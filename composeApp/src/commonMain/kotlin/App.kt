import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.oldguy.common.io.File
import config.initConfig
import kotlinx.coroutines.runBlocking
import okio.FileSystem


@Composable
fun App() {
    var selectedFiles by remember { mutableStateOf(emptyList<String>()) }
    var showContent by remember { mutableStateOf(false) }
    val homeDir = File("/data/data/org.example.tyf.android").path
    println(homeDir)
    runBlocking { initConfig(homeDir) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // File Choosing Form
        FileChooser() { files -> selectedFiles = files }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        Button(
            onClick = {
                if (selectedFiles.isNotEmpty()) {
                    // Assuming TrackRepository has an exec function that takes a list of Uris
//                    TrackRepository.exec(context, selectedFiles)
                    showContent = true
                }
            },
            enabled = selectedFiles.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedFiles.isNotEmpty()) Color.Green else Color.Gray
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply" + homeDir)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content Display
        AnimatedVisibility(showContent) {
            // Assuming you want to display something here after applying
            // Modify this part based on your actual content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
                Text("Content Applied!")
                Text(selectedFiles.joinToString("\n"))
            }
        }
    }
}

@Composable
fun FileChooser(
    onFilesSelected: (List<String>) -> Unit
) {
    var showFilePicker by remember { mutableStateOf(false) }
    var showDirPicker by remember { mutableStateOf(false) }
    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { files ->
        showFilePicker = false
        files?.let { mpFiles -> onFilesSelected(mpFiles.map{ it.path }) }
    }
    Button(
        onClick = {
            showFilePicker = true
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Choose Files")
        }
    }
    DirectoryPicker(showDirPicker) { path ->
        showDirPicker = false
        path?.let { onFilesSelected(listOf(it)) }
    }
    Button(
        onClick = {
            showDirPicker = true
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Choose Directory")
        }
    }
}

//@Composable
//fun SelectedFilesList(files: List<Uri>) {
//    LazyColumn {
//        items(files) { file ->
//            FileItem(file)
//        }
//    }
//}
//
//@Composable
//fun FileItem(file: Uri) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(50.dp)
//            .padding(8.dp)
//            .background(MaterialTheme.colors.primary)
//            .clip(MaterialTheme.shapes.medium)
//            .clickable { /* Handle item click if needed */ }
//    ) {
//        Text(
//            text = file.toString(),
//            modifier = Modifier
//                .padding(8.dp)
//                .align(Alignment.CenterStart)
//        )
//    }
//}
