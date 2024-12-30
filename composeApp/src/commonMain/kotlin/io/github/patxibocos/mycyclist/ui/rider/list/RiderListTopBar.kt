package io.github.patxibocos.mycyclist.ui.rider.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import io.github.patxibocos.mycyclist.ui.rider.list.RiderListViewModel.Sorting
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    topBarState: RiderListViewModel.TopBarState,
    focusManager: FocusManager,
    onSortingSelected: (Sorting) -> Unit,
    onSearched: (String) -> Unit,
    onToggled: () -> Unit,
    onClicked: suspend () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var showKeyboard by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue(topBarState.search)) }
    TopAppBar(
        title = {
            Title(
                searching = topBarState.searching,
                focusRequester = focusRequester,
                focusManager = focusManager,
                searchText = searchText,
                showKeyboard = showKeyboard,
                onValueChange = { search ->
                    searchText = search
                    onSearched(search.text)
                },
                onHideKeyboard = {
                    showKeyboard = false
                },
                onClicked = onClicked,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if (!topBarState.searching) {
                    showKeyboard = true
                } else {
                    searchText = TextFieldValue("")
                }
                onToggled()
            }) {
                val icon =
                    if (topBarState.searching) Icons.Outlined.Close else Icons.Outlined.Search
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        actions = {
            Box {
                var sortingOptionsVisible by remember { mutableStateOf(false) }
                IconButton(onClick = { sortingOptionsVisible = true }) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.List, contentDescription = null)
                }
                val coroutineScope = rememberCoroutineScope()
                SortingMenu(
                    expanded = sortingOptionsVisible,
                    selectedSorting = topBarState.sorting,
                    onSortingSelected = { sorting ->
                        sortingOptionsVisible = false
                        coroutineScope.launch {
                            delay(timeMillis = 50)
                            onSortingSelected(sorting)
                        }
                    },
                    onDismissed = { sortingOptionsVisible = false },
                )
            }
        },
    )
}

@Composable
private fun Title(
    searching: Boolean,
    focusRequester: FocusRequester,
    searchText: TextFieldValue,
    focusManager: FocusManager,
    showKeyboard: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    onHideKeyboard: () -> Unit,
    onClicked: suspend () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    AnimatedContent(searching, label = "RidersTopAppBarAnimatedContent") {
        if (it) {
            TextField(
                value = searchText,
                onValueChange = onValueChange,
                placeholder = {
                    Text("Search")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.focusRequester(focusRequester).fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                ),
            )
            if (showKeyboard) {
                onHideKeyboard()
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            LaunchedEffect(Unit) {
                focusManager.clearFocus()
            }
            Text(
                text = "Riders",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        coroutineScope.launch {
                            onClicked()
                        }
                    }
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SortingMenu(
    expanded: Boolean,
    selectedSorting: Sorting,
    onSortingSelected: (Sorting) -> Unit,
    onDismissed: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissed,
    ) {
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.UciRanking)
            },
            enabled = selectedSorting != Sorting.UciRanking,
            text = {
                Text("UCI Ranking")
            },
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.LastName)
            },
            enabled = selectedSorting != Sorting.LastName,
            text = {
                Text("Name")
            },
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.Country)
            },
            enabled = selectedSorting != Sorting.Country,
            text = {
                Text("Country")
            },
        )
    }
}
