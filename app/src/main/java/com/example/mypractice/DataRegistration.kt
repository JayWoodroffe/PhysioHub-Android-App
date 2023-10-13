package com.example.mypractice

object DataRegistration {
    /*setters for the fields
    each setter checks the values to make sure they are valid
    returns an error with the appropriate error message if not
     */
     var name: String = ""
        set(value)
        {
            var temp = name.trim()
            if (value == "")
            {
                throw IllegalArgumentException("Name cannot be null")
            }
            else{
                field = value
            }
        }

     var email: String = ""
        set(value) {
            val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,4})+$")
            if (value.matches(emailRegex))
            {
                field = value
            }
            else
            {
                throw IllegalArgumentException("Invalid Email")
            }
        }
     var phone: String = ""
        set(value) {
            var temp: String = value.replace("\\s+".toRegex(), "")//removing all the spaces in the string
            var length = temp.length
            var alldig = temp.all{it.isDigit()}
            if (alldig&& length == 10)
            {
                field = temp
            }
            else
            {
                throw IllegalArgumentException("Invalid Number")
            }

        }
     var certID: String = ""
         set(value) {
             if(value.trim().equals(""))
             {
                 throw IllegalArgumentException("ID must be given")
             }
             else{
                 field = value
             }
         }
     var pracID: String = ""
         set(value) {
             if(value.trim().equals(""))
             {
                 throw IllegalArgumentException("ID must be given")
             }
             else{
                 field = value
             }
         }

     var password: String = ""
         set(value) {
             val specialCharRegex = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
             val digitRegex = Regex("\\d")
             val pTemp = value.trim()

             //checking if the password matches the requirements for a safe password
             var valid: Boolean = true
             if( pTemp.length < 8 )
             {
                 throw IllegalArgumentException("Password too short. Must be 8 characters or more.")
                 valid = false
             }
             if (!specialCharRegex.containsMatchIn(pTemp))
             {
                 throw IllegalArgumentException("Password should contain a special character")
                 valid = false
             }
             if(!digitRegex.containsMatchIn(pTemp)) {
                 throw IllegalArgumentException("Password should contain a digit")
                 valid = false
             }
             if (valid)
             {
                 field = pTemp
             }
         }

    //adding the complete data to the database

}