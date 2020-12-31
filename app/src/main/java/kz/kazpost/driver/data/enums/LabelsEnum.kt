package kz.kazpost.driver.data.enums

enum class LabelsEnum(val ru: String, val t: String) {
    emsBag("Мешки и посылки \"EMS\"", "emsBag"),
    strBag("Мешки \"Сақтандыру\"", "strBag"),
    prvBag("Мешки и посылки \"Правительственные\"", "prvBag"),
    korBag("Мешки с корреспонденцией", "korBag"),
    gazeta("Газеты", "gazeta"),
    kgpo("Крупногабаритные почтовые отправления", "kgpo"),
    taraBag("Порожняя тара", "taraBag"),
    rpo("Посылки", "rpo"),
    otherBag("Прочие", "otherBag")
}