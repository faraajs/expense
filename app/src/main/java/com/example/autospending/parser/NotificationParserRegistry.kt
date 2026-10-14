package com.example.autospending.parser

/**
 * Registry of all bank parsers. Finds the first matching parser for a given package name.
 * Order matters: more specific parsers should come before generic ones.
 */
object NotificationParserRegistry {
    private val parsers: List<BaseBankParser> = listOf(
        GoPayParser(),
        BluParser(),
        SeaBankParser(),
        ShopeeParser(),
        TokopediaParser(),
        DanaParser(),
        OvoParser(),
        BcaParser(),
        BrimoParser(),
        LivinParser(),
        MandiriOnlineParser(),
        JagoParser(),
        GrabParser(),
        ByUParser(),
        TelkomselParser()
    )

    fun parse(payload: NotificationPayload) =
        parsers.firstOrNull { it.handles(payload.packageName) }?.parse(payload)

    fun isKnownPackage(packageName: String): Boolean =
        parsers.any { it.handles(packageName) }

    val allPackagePrefixes: List<String> =
        parsers.flatMap { it.packagePrefixes }
}
