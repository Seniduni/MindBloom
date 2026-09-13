package com.mindbloom.app.data


/**
 * Content that ships with the app rather than living in the database:
 * the meditation library and the badge set shown on the profile.
 */
object StaticContent {

    val meditationCategories = listOf("All", "Sleep", "Focus", "Anxiety", "Breathing")

    val meditationSessions = listOf(
        MeditationSession(
            id = 1, title = "Deep Sleep", category = "Sleep", minutes = 20,
            emoji = "\uD83C\uDF19", gradientStart = 0xFF6B4EE6, gradientEnd = 0xFF9B7BF0
        ),
        MeditationSession(
            id = 2, title = "Focus & Concentration", category = "Focus", minutes = 15,
            emoji = "\uD83C\uDFAF", gradientStart = 0xFF22C1E0, gradientEnd = 0xFF2DD4BF
        ),
        MeditationSession(
            id = 3, title = "Anxiety Relief", category = "Anxiety", minutes = 10,
            emoji = "\uD83C\uDF0A", gradientStart = 0xFF2DD4BF, gradientEnd = 0xFF5FD6A8
        ),
        MeditationSession(
            id = 4, title = "Morning Energy", category = "Energy", minutes = 10,
            emoji = "\uD83C\uDF05", gradientStart = 0xFFF5A623, gradientEnd = 0xFFE8B33A
        ),
        MeditationSession(
            id = 5, title = "Breathing Exercise", category = "Breathing", minutes = 5,
            emoji = "\uD83E\uDEC1", gradientStart = 0xFF8B6FF0, gradientEnd = 0xFFB39BF5
        )
    )

    val achievements = listOf(
        Achievement(
            emoji = "\uD83D\uDD25", label = "7 Day", backgroundColor = 0xFFFDF0D8,
            description = "Kept every habit alive for a full week."
        ),
        Achievement(
            emoji = "\uD83C\uDF31", label = "Starter", backgroundColor = 0xFFD8F3E2,
            description = "Created your first habit and logged it."
        ),
        Achievement(
            emoji = "\uD83E\uDDD8", label = "Zen", backgroundColor = 0xFFEDE9FE,
            description = "Finished ten meditation sessions."
        ),
        Achievement(
            emoji = "\uD83D\uDCD6", label = "Reader", backgroundColor = 0xFFDCEAFE,
            description = "Read on twenty separate days."
        ),
        Achievement(
            emoji = "\uD83C\uDFC6", label = "Champ", backgroundColor = 0xFFFCE1E4,
            description = "Reached a fourteen day personal best."
        )
    )

    val wellnessGoals = listOf(
        "Reduce Stress",
        "Sleep Better",
        "Build Focus",
        "Stay Active",
        "Feel Happier"
    )

    val habitEmojis = listOf(
        "\uD83D\uDCA7", "\uD83E\uDDD8", "\uD83D\uDCD6", "\uD83D\uDEB6", "\uD83D\uDCAA",
        "\uD83D\uDE34", "\uD83C\uDFC3", "\uD83E\uDD57", "\uD83C\uDFB5", "\u270D\uFE0F",
        "\uD83C\uDF3F", "\u2615"
    )

    /** Accent / background colour pairs offered when creating a habit. */
    val habitPalettes = listOf(
        0xFF3B82F6 to 0xFFDCEAFE,
        0xFF6B4EE6 to 0xFFEDE9FE,
        0xFFF59E0B to 0xFFFDF3D5,
        0xFF22B85C to 0xFFD8F3E2,
        0xFFEF4444 to 0xFFFCE1E4,
        0xFF2DD4BF to 0xFFD6F7F0
    )
}
