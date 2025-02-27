package voice.data.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import voice.data.Book
import voice.data.BookContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository
@Inject constructor(
  private val chapterRepo: ChapterRepo,
  private val contentRepo: BookContentRepo,
) {

  private var warmedUp = false
  private val warmupMutex = Mutex()

  private suspend fun warmUp() {
    if (warmedUp) return
    warmupMutex.withLock {
      if (warmedUp) return@withLock
      val chapters = contentRepo.all()
        .filter { it.isActive }
        .flatMap { it.chapters }
      chapterRepo.warmup(chapters)
      warmedUp = true
    }
  }

  fun flow(): Flow<List<Book>> {
    return contentRepo.flow()
      .map { contents ->
        contents.filter { it.isActive }
          .map { content ->
            content.book()
          }
      }
  }

  suspend fun all(): List<Book> {
    return contentRepo.all()
      .filter { it.isActive }
      .map { it.book() }
  }

  fun flow(id: Book.Id): Flow<Book?> {
    return contentRepo.flow(id)
      .map { it?.book() }
  }

  suspend fun get(id: Book.Id): Book? {
    return contentRepo.get(id)?.book()
  }

  suspend fun updateBook(id: Book.Id, update: (BookContent) -> BookContent) {
    val content = contentRepo.get(id) ?: return
    val updated = update(content)
    contentRepo.put(updated)
  }

  private suspend fun BookContent.book(): Book {
    warmUp()
    return Book(
      content = this,
      chapters = chapters.map { chapterId ->
        chapterRepo.get(chapterId)!!
      }
    )
  }
}
