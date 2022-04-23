package com.kukki.firebasesample.ui.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kukki.firebasesample.R
import com.kukki.firebasesample.ui.theme.TitleAppBar


enum class TopNavOption {
    USER_LIST, PRODUCT_LIST, ADD_PRODUCT
}

@Composable
fun TopTabView(
    bgColor: Color, selectedNav: TopNavOption, navList: List<TopNavOption>,
    modifier: Modifier = Modifier, onClick: (TopNavOption) -> Unit
) {
    val selectedIndex = navList.indexOfFirst { it == selectedNav }
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        backgroundColor = bgColor,
    ) {
        navList.forEachIndexed { index, tabOption ->
            val selected = index == selectedIndex
            Tab(selected, onClick = {
                onClick(tabOption)
            }) {
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(getTabNavTitle(tabOption)),
                        style = if (selected) TitleAppBar else TitleAppBar,
                    )
                }
            } // end Tab
        } // end forEach
    } // end TabRow
}

private fun getTabNavTitle(tabOption: TopNavOption): Int {
    return when (tabOption) {
        TopNavOption.USER_LIST -> R.string.user_list
        TopNavOption.PRODUCT_LIST -> R.string.product_list
        TopNavOption.ADD_PRODUCT -> R.string.add_product
    }
}