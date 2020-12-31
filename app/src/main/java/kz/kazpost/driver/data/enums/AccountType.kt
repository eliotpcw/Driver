package kz.kazpost.driver.data.enums

import java.io.Serializable

sealed class AccountType : Serializable{
    object Mercenary : AccountType()
    object Driver : AccountType()
}