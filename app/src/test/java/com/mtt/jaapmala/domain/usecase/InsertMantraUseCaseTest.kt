import com.google.common.truth.Truth.assertThat
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.domain.usecase.jaap.InsertMantraUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsertMantraUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var insertMantraUseCase: InsertMantraUseCase

    @Before
    fun setUp() {
        repository = mockk()
        insertMantraUseCase = InsertMantraUseCase(repository)
    }

    @Test
    fun `invoke should insert mantra and return MantraDto`() = runTest {
        // Arrange
        val mantraDto = MantraDto(
            id = 1,
            name = "Gayatri Mantra",
            date = "2025-09-12",
            malaSize = 108,
            todayCount = 20,
            malaCount = 2,
            lifetimeCount = 400,
            lifetimeMalaCount = 3,
            currentCount = 5
        )
        coEvery { repository.insertMantra("Gayatri Mantra", "2025-09-12", 108) } returns mantraDto

        // Act
        val result = insertMantraUseCase("Gayatri Mantra", "2025-09-12", 108)

        // Assert
        assertThat(result).isEqualTo(mantraDto)
        coVerify { repository.insertMantra("Gayatri Mantra", "2025-09-12", 108) }
    }

    @Test
    fun `invoke should handle insertion failure`() = runTest {
        // Arrange
        coEvery { repository.insertMantra("Invalid", "2025-09-12", 108) } throws Exception("DB insert failed")

        try {
            // Act
            insertMantraUseCase("Invalid", "2025-09-12", 108)
            assert(false) { "Exception was expected but not thrown" }
        } catch (e: Exception) {
            // Assert
            assertThat(e).hasMessageThat().contains("DB insert failed")
        }
    }
}
