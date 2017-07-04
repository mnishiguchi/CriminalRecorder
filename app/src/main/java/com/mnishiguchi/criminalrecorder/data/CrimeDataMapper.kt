package com.mnishiguchi.criminalrecorder.data

import com.mnishiguchi.criminalrecorder.domain.Crime
import java.util.*

/**
 * Perform the conversion between a database entity object and a domain object.
 */
object CrimeDataMapper {

    // domain model => db entity
    fun fromDomain(crime: Crime): CrimeEntity = with(crime) {
        CrimeEntity(
                _id = _id,
                uuid = uuid.toString(),
                title = title,
                date = date,
                isSolved = if (isSolved) 1 else 0,
                suspect = suspect
        )
    }

    // db entity => domain model
    fun toDomain(crimeEntity: CrimeEntity): Crime = with(crimeEntity) {
        Crime(
                _id = _id,
                uuid = UUID.fromString(uuid), // String -> UUID
                title = title,
                date = date,
                isSolved = isSolved == 1, // Int -> Boolean
                suspect = suspect
        )
    }
}