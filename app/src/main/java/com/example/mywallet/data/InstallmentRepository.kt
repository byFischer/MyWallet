package com.example.mywallet.data

import com.example.mywallet.dao.InstallmentDao
import com.example.mywallet.data.model.Installment
import kotlinx.coroutines.flow.Flow

class InstallmentRepository(private val dao: InstallmentDao) {
    val allInstallments : Flow<List<Installment>> = dao.getAll()

    suspend fun insert(installment: Installment): Long {
        return dao.insert(installment)
    }

    suspend fun update(installment: Installment) {
        dao.update(installment)
    }

    suspend fun delete(installment: Installment) {
        dao.delete(installment)
    }

    suspend fun getById(id: Long): Installment? {
        return dao.getById(id)
    }
}