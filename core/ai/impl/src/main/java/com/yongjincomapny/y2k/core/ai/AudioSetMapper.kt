package com.yongjincomapny.y2k.core.ai

/**
 * YAMNet AudioSet 521 클래스 중 음악 관련 클래스를 장르/무드로 매핑한다.
 * 참고: https://github.com/tensorflow/models/blob/master/research/audioset/yamnet/yamnet_class_map.csv
 */
object AudioSetMapper {

    private val genreMapping: Map<String, String> = mapOf(
        // 주요 장르
        "Pop music" to "Pop",
        "Hip hop music" to "Hip-Hop",
        "Rock music" to "Rock",
        "Rock and roll" to "Rock",
        "Rhythm and blues" to "R&B",
        "Soul music" to "Soul",
        "Reggae" to "Reggae",
        "Country" to "Country",
        "Funk" to "Funk",
        "Folk music" to "Folk",
        "Jazz" to "Jazz",
        "Blues" to "Blues",
        "Classical music" to "Classical",
        "Electronic music" to "Electronic",
        "House music" to "House",
        "Techno" to "Techno",
        "Dubstep" to "Dubstep",
        "Drum and bass" to "DnB",
        "Electronica" to "Electronic",
        "Electronic dance music" to "EDM",
        "Disco" to "Disco",
        "Punk rock" to "Punk",
        "Heavy metal" to "Metal",
        "Grunge" to "Grunge",
        "Progressive rock" to "Progressive",
        "Psychedelic rock" to "Psychedelic",
        "Alternative rock" to "Alternative",
        "Indie rock" to "Indie",
        "New-age music" to "New Age",
        "Ambient music" to "Ambient",
        "Trance music" to "Trance",
        "Ska" to "Ska",
        "Flamenco" to "Flamenco",
        "Gospel music" to "Gospel",
        "Opera" to "Opera",
        "Swing music" to "Swing",
        "Bluegrass" to "Bluegrass",
        // 추가 장르
        "Music of Latin America" to "Latin",
        "Salsa music" to "Latin",
        "Middle Eastern music" to "World",
        "Music of Africa" to "World",
        "Music of Asia" to "World",
        "Afrobeat" to "Afrobeat",
        "Christian music" to "Gospel",
        "Vocal music" to "Vocal",
        "Music for children" to "Kids",
        "Soundtrack music" to "Soundtrack",
        "Theme music" to "Soundtrack",
        "Christmas music" to "Holiday",
        "Wedding music" to "Wedding",
        "Lullaby" to "Lullaby",
        // 보컬/래핑
        "Rapping" to "Hip-Hop",
        "Beatboxing" to "Hip-Hop",
    )

    private val moodMapping: Map<String, String> = mapOf(
        "Happy music" to "Happy",
        "Sad music" to "Sad",
        "Tender music" to "Tender",
        "Exciting music" to "Energetic",
        "Angry music" to "Aggressive",
        "Scary music" to "Dark",
    )

    // 악기 감지로 장르 힌트 보강 (confidence가 높을 때만)
    private val instrumentHints: Map<String, String> = mapOf(
        "Synthesizer" to "Electronic",
        "Drum machine" to "Electronic",
        "Electric guitar" to "Rock",
        "Acoustic guitar" to "Acoustic",
        "Piano" to "Classical",
        "Electric piano" to "R&B",
        "Violin, fiddle" to "Classical",
        "Saxophone" to "Jazz",
        "Trumpet" to "Jazz",
        "Double bass" to "Jazz",
        "Singing bowl" to "Ambient",
    )

    private val fallbackGenreMapping: Map<String, String> = mapOf(
        "Music" to "Other",
        "Musical instrument" to "Instrumental",
        "Singing" to "Vocal",
        "Choir" to "Vocal",
        "Chant" to "Vocal",
    )

    fun mapToGenres(className: String): String? {
        return genreMapping[className]
            ?: instrumentHints[className]
            ?: fallbackGenreMapping[className]
    }

    fun mapToMood(className: String): String? {
        return moodMapping[className]
    }
}
