package voice.bookOverview.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import voice.bookOverview.BookOverviewCategory
import voice.bookOverview.BookOverviewViewState
import voice.bookOverview.R
import voice.common.compose.ImmutableFile
import voice.common.compose.LongClickableCard
import voice.common.compose.plus
import voice.common.recomposeHighlighter
import voice.data.Book

@Composable
internal fun ListBooks(
  contentPadding: PaddingValues,
  books: Map<BookOverviewCategory, List<BookOverviewViewState.Content.BookViewState>>,
  onBookClick: (Book.Id) -> Unit,
  onBookLongClick: (Book.Id) -> Unit,
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = contentPadding + PaddingValues(top = 24.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
  ) {
    books.forEach { (category, books) ->
      if (books.isEmpty()) return@forEach
      stickyHeader(
        key = category,
        contentType = "header"
      ) {
        Header(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp, horizontal = 8.dp),
          category = category
        )
      }
      items(
        items = books,
        key = { it.id.value },
        contentType = { "item" }
      ) { book ->
        ListBookRow(
          book = book,
          onBookClick = onBookClick,
          onBookLongClick = onBookLongClick,
        )
      }
    }
  }
}

@Composable
private fun ListBookRow(
  modifier: Modifier = Modifier,
  book: BookOverviewViewState.Content.BookViewState,
  onBookClick: (Book.Id) -> Unit,
  onBookLongClick: (Book.Id) -> Unit,
) {
  LongClickableCard(
    onClick = {
      onBookClick(book.id)
    },
    onLongClick = {
      onBookLongClick(book.id)
    },
    modifier = modifier
      .recomposeHighlighter()
      .fillMaxWidth()
  ) {
    Column {
      Row {
        CoverImage(book.cover)
        Column(
          Modifier
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .align(Alignment.CenterVertically)
        ) {
          if (book.author != null) {
            Text(
              text = book.author.toUpperCase(LocaleList.current),
              style = MaterialTheme.typography.labelSmall,
              maxLines = 1
            )
          }
          Text(
            text = book.name,
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            text = book.remainingTime,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }
    if (book.progress > 0.05) {
      LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        progress = book.progress
      )
    }
  }
}

@Composable
private fun CoverImage(cover: ImmutableFile?) {
  AsyncImage(
    modifier = Modifier
      .recomposeHighlighter()
      .padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
      .size(76.dp)
      .clip(RoundedCornerShape(8.dp)),
    model = cover?.file,
    placeholder = painterResource(id = R.drawable.album_art),
    error = painterResource(id = R.drawable.album_art),
    contentDescription = null
  )
}
