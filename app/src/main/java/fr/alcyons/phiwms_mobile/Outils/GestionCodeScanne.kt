package fr.alcyons.phiwms_mobile.Outils

import java.util.Locale

 class GestionCodeScanne {

    companion object {
        @JvmStatic
        fun decoupageCode(coderecue: String): HashMap<String, String> {
            var type = ""
            var code = ""
            var lot = ""
            var peremption = ""
            var serie = ""
            var emplacement = ""
            var codeinconnu = ""


            if (coderecue.startsWith("01") || coderecue.startsWith("02")) {
                val resultDecoupage: GS1Parser.GS1Result = GS1Parser.parseGS1Code(coderecue)
                type = "GS1"
                code = resultDecoupage.productCode
                lot = resultDecoupage.lotNumber
                peremption = resultDecoupage.expirationDateAffichage
                serie = resultDecoupage.serie
            } else if (coderecue.startsWith("+")) {
                val resultDecoupage: HIBCParser.HIBCResult = HIBCParser.parseHIBCCode(coderecue)
                type = "HIBC"
                code = resultDecoupage.productCode
                lot = resultDecoupage.lotNumber
                peremption = resultDecoupage.expirationDate
                serie = resultDecoupage.serie
            } else if (coderecue.uppercase(Locale.getDefault()).startsWith("PHITAGPLACE")) {
                val arrayPlace = coderecue.split(":")
                val idEmplacement = arrayPlace[arrayPlace.size - 1]
                type = "Emplacement"
                emplacement = idEmplacement
            } else if (coderecue.uppercase(Locale.getDefault()).startsWith("PHITAGTIN")) {
                val arrayPlace = coderecue.split(":")
                val gtin = arrayPlace[arrayPlace.size - 1]
                type = "GTIN"
                code = gtin
            } else if (coderecue.uppercase(Locale.getDefault()).startsWith("PHITAGREF")) {
                val arrayPlace = coderecue.split(":")
                val idreference = arrayPlace[arrayPlace.size - 1]
                type = "IdProduit"
                code = idreference
            } else {
                codeinconnu = coderecue
            }

            var mapRetourner = HashMap<String, String>()
            mapRetourner.put("type", type)
            mapRetourner.put("code", code)
            mapRetourner.put("lot", lot)
            mapRetourner.put("peremption", peremption)
            mapRetourner.put("serie", serie)
            mapRetourner.put("emplacement", emplacement)
            mapRetourner.put("codeinconnu", codeinconnu)

            return mapRetourner
        }
    }
}