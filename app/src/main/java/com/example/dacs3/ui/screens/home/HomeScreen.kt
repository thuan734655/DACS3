package com.example.dacs3.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs3.models.HomeResponse
import com.example.dacs3.viewmodel.HomeViewModel
import com.example.dacs3.viewmodel.UiState

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.homeState.collectAsState()

    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> {
            val data = (state as UiState.Success<HomeResponse>).data
            Text("Server says: ${data.message}")
        }
        is UiState.Error -> Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
        else -> {}
    }

    // Bố cục chính sử dụng ConstraintLayout
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (loader, errorText, topElement, channels, chat, input) = createRefs()

        // Loader khi đang tải
        if (state is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loader) {
                    centerTo(parent)
                }
            )
            return@ConstraintLayout
        }

        // Thông báo lỗi
        if (state is UiState.Error) {
            val err = (state as UiState.Error).message
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.constrainAs(errorText) {
                    centerTo(parent)
                }
            )
            return@ConstraintLayout
        }

        // Khi có dữ liệu
        HomeTopBar(
            modifier = Modifier.constrainAs(topElement) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        ChannelList(
            modifier = Modifier.constrainAs(channels) {
                top.linkTo(parent.top)
                bottom.linkTo(input.top)
                start.linkTo(parent.start)
                width = Dimension.value(250.dp)
                height = Dimension.fillToConstraints
            }
        )

        ChatWindow(
            modifier = Modifier.constrainAs(chat) {
                top.linkTo(parent.top)
                bottom.linkTo(input.top)
                start.linkTo(channels.end)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )

        MessageInputBar(
            modifier = Modifier.constrainAs(input) {
                bottom.linkTo(parent.bottom)
                start.linkTo(channels.end)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}
