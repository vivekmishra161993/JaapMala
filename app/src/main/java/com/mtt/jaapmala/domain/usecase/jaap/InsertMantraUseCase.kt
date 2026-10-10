package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.domain.repository.JaapRepository
import javax.inject.Inject

class InsertMantraUseCase @Inject constructor(private val repo: JaapRepository) {
    suspend operator fun invoke(name:String,date:String,size:Int): MantraDto {
       return repo.insertMantra(name,date,size)
    }
}