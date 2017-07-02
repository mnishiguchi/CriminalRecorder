package com.mnishiguchi.criminalrecorder.data

import com.mnishiguchi.criminalrecorder.domain.Crime as DomainCrime

/**
 * Created by masa on 7/2/17.
 */
class CrimeDataMapper {
    companion object {
        /**
         * domain model => db entity
         */
        fun fromDomain(crime: DomainCrime): Crime {
            return Crime(
                    uuid = crime.uuid,
                    title = crime.title,
                    date = crime.date,
                    isSolved = crime.isSolved
            )
        }

        /**
         * db entity => domain model
         */
        fun toDomain(crime: Crime): DomainCrime {
            return DomainCrime(
                    uuid = crime.uuid,
                    title = crime.title,
                    date = crime.date,
                    isSolved = crime.isSolved
            )
        }
    }
}