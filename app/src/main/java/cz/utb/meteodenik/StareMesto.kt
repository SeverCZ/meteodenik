package cz.utb.meteodenik

class StareMesto(var nazev: String?, val teplota: Double?) {
    fun popis(): String {
        if (nazev != null && teplota != null) {
            return nazev + ": " + teplota
        }
        return "neznámé"
    }
}