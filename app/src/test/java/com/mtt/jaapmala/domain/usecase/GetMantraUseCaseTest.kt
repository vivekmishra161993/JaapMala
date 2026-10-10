
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.domain.usecase.jaap.GetMantraUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMantraUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var getMantraUseCase: GetMantraUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getMantraUseCase = GetMantraUseCase(repository)
    }

    @Test
    fun `invoke should return mantra for given id`() = runTest {
        // Arrange
        val mantra = JaapEntity(
            id = 1,
            name = "Gayatri Mantra",
            count = 108,
            malaSize = 108,
            date = "2025-09-12"
        )

        coEvery { repository.getMantra(1) } returns flowOf(mantra)

        // Act + Assert
        getMantraUseCase(1).test {
            val result = awaitItem()
            assertThat(result.id).isEqualTo(1)
            assertThat(result.name).isEqualTo("Gayatri Mantra")
            assertThat(result.count).isEqualTo(108)
            assertThat(result.date).isEqualTo("2025-09-12")
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty flow when mantra not found`() = runTest {
        // Arrange
        coEvery { repository.getMantra(999) } returns emptyFlow()

        // Act + Assert
        getMantraUseCase(999).test {
            awaitComplete() // nothing should be emitted
        }
    }
}
