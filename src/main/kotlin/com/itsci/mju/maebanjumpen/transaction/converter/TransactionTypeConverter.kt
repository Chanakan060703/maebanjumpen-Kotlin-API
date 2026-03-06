package com.itsci.mju.maebanjumpen.transaction.converter

import com.itsci.mju.maebanjumpen.transaction.constant.TransactionTypeEnum
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class TransactionTypeConverter : AttributeConverter<TransactionTypeEnum, Int> {
    override fun convertToDatabaseColumn(attribute: TransactionTypeEnum?): Int? = attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): TransactionTypeEnum? {
        return dbData?.let {
            TransactionTypeEnum.fromValue(it)
                ?: throw IllegalArgumentException("Unknown transaction type value: $dbData")
        }
    }
}