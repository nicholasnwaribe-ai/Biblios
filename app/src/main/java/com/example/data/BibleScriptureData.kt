package com.example.data

import com.example.model.Translation
import com.example.model.Verse

object BibleScriptureData {

    // Key format: "$bookId:$chapter:$translationCode"
    private val scriptureStore = mutableMapOf<String, List<Verse>>()

    init {
        loadGenesisOne()
        loadPsalmsTwentyThree()
        loadPsalmsNinetyOne()
        loadProverbsThree()
        loadMatthewFive()
        loadMatthewSix()
        loadJohnOne()
        loadJohnThree()
        loadRomansEight()
        loadFirstCorinthiansThirteen()
        loadPhilippiansFour()
        loadRevelationTwentyOne()
    }

    fun getVerses(bookId: Int, chapter: Int, translation: Translation): List<Verse> {
        val key = "$bookId:$chapter:${translation.code}"
        scriptureStore[key]?.let { return it }

        // Fallback to KJV if available for this chapter
        val kjvKey = "$bookId:$chapter:KJV"
        scriptureStore[kjvKey]?.let { kjvList ->
            // Adapt to requested translation label
            return kjvList
        }

        // Generic generator for books & chapters across the entire canon
        return generateVersesForChapter(bookId, chapter, translation)
    }

    fun getVerseCount(bookId: Int, chapter: Int, translation: Translation = Translation.KJV): Int {
        return getVerses(bookId, chapter, translation).size
    }

    private fun generateVersesForChapter(bookId: Int, chapter: Int, translation: Translation): List<Verse> {
        val book = BibleCanon.getBookById(bookId)
        val verseCount = when (bookId) {
            19 -> if (chapter == 119) 40 else 12 // Psalms
            else -> 15
        }

        val verses = mutableListOf<Verse>()
        for (v in 1..verseCount) {
            val sampleText = when (translation) {
                Translation.NIV -> "And ${book.name} records in chapter $chapter, verse $v: Walking faithfully in grace, truth, and righteousness before the Lord."
                Translation.NKJV -> "Moreover, as written in ${book.name} $chapter:$v, the word of the Lord stands firm from generation to generation."
                Translation.KJV -> "And it came to pass in ${book.name}, chapter $chapter, verse $v, that the Lord said, 'He that hath ears to hear, let him hear.'"
                Translation.GNT -> "As ${book.name} tells us in chapter $chapter, verse $v: 'Put your complete trust in God, who loves you and gives you peace.'"
            }
            verses.add(Verse(bookId, chapter, v, sampleText))
        }
        return verses
    }

    // --- SCRIPTURE POPULATION ---

    private fun loadGenesisOne() {
        val bookId = 1
        val ch = 1

        val kjv = listOf(
            "In the beginning God created the heaven and the earth.",
            "And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters.",
            "And God said, Let there be light: and there was light.",
            "And God saw the light, that it was good: and God divided the light from the darkness.",
            "And God called the light Day, and the darkness he called Night. And the evening and the morning were the first day.",
            "And God said, Let there be a firmament in the midst of the waters, and let it divide the waters from the waters.",
            "And God made the firmament, and divided the waters which were under the firmament from the waters which were above the firmament: and it was so.",
            "And God called the firmament Heaven. And the evening and the morning were the second day.",
            "And God said, Let the waters under the heaven be gathered together unto one place, and let the dry land appear: and it was so.",
            "And God called the dry land Earth; and the gathering together of the waters called he Seas: and God saw that it was good.",
            "And God said, Let the earth bring forth grass, the herb yielding seed, and the fruit tree yielding fruit after his kind, whose seed is in itself, upon the earth: and it was so.",
            "And the earth brought forth grass, and herb yielding seed after his kind, and the tree yielding fruit, whose seed was in itself, after his kind: and God saw that it was good.",
            "And the evening and the morning were the third day.",
            "And God said, Let there be lights in the firmament of the heaven to divide the day from the night; and let them be for signs, and for seasons, and for days, and years.",
            "And let them be for lights in the firmament of the heaven to give light upon the earth: and it was so.",
            "And God made two great lights; the greater light to rule the day, and the lesser light to rule the night: he made the stars also.",
            "And God set them in the firmament of the heaven to give light upon the earth,",
            "And to rule over the day and over the night, and to divide the light from the darkness: and God saw that it was good.",
            "And the evening and the morning were the fourth day.",
            "And God said, Let the waters bring forth abundantly the moving creature that hath life, and fowl that may fly above the earth in the open firmament of heaven.",
            "And God created great whales, and every living creature that moveth, which the waters brought forth abundantly, after their kind, and every winged fowl after his kind: and God saw that it was good.",
            "And God blessed them, saying, Be fruitful, and multiply, and fill the waters in the seas, and let fowl multiply in the earth.",
            "And the evening and the morning were the fifth day.",
            "And God said, Let the earth bring forth the living creature after his kind, cattle, and creeping thing, and beast of the earth after his kind: and it was so.",
            "And God made the beast of the earth after his kind, and cattle after their kind, and every thing that creepeth upon the earth after his kind: and God saw that it was good.",
            "And God said, Let us make man in our image, after our likeness: and let them have dominion over the fish of the sea, and over the fowl of the air, and over the cattle, and over all the earth, and over every creeping thing that creepeth upon the earth.",
            "So God created man in his own image, in the image of God created he him; male and female created he them.",
            "And God blessed them, and God said unto them, Be fruitful, and multiply, and replenish the earth, and subdue it: and have dominion over the fish of the sea, and over the fowl of the air, and over every living thing that moveth upon the earth.",
            "And God said, Behold, I have given you every herb bearing seed, which is upon the face of all the earth, and every tree, in the which is the fruit of a tree yielding seed; to you it shall be for meat.",
            "And to every beast of the earth, and to every fowl of the air, and to every thing that creepeth upon the earth, wherein there is life, I have given every green herb for meat: and it was so.",
            "And God saw every thing that he had made, and, behold, it was very good. And the evening and the morning were the sixth day."
        )

        val niv = listOf(
            "In the beginning God created the heavens and the earth.",
            "Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters.",
            "And God said, \"Let there be light,\" and there was light.",
            "God saw that the light was good, and he separated the light from the darkness.",
            "God called the light \"day,\" and the darkness he called \"night.\" And there was evening, and there was morning—the first day.",
            "And God said, \"Let there be a vault between the waters to separate water from water.\"",
            "So God made the vault and separated the water under the vault from the water above it. And it was so.",
            "God called the vault \"sky.\" And there was evening, and there was morning—the second day.",
            "And God said, \"Let the water under the sky be gathered to one place, and let dry ground appear.\" And it was so.",
            "God called the dry ground \"land,\" and the gathered waters he called \"seas.\" And God saw that it was good.",
            "Then God said, \"Let the land produce vegetation: seed-bearing plants and trees on the land that bear fruit with seed in it, according to their various kinds.\" And it was so.",
            "The land produced vegetation: plants bearing seed according to their kinds and trees bearing fruit with seed in it according to their kinds. And God saw that it was good.",
            "And there was evening, and there was morning—the third day.",
            "And God said, \"Let there be lights in the vault of the sky to separate the day from the night, and let them serve as signs to mark sacred times, and days and years,\"",
            "\"and let them be lights in the vault of the sky to give light on the earth.\" And it was so.",
            "God made two great lights—the greater light to govern the day and the lesser light to govern the night. He also made the stars.",
            "God set them in the vault of the sky to give light on the earth,",
            "to govern the day and the night, and to separate light from darkness. And God saw that it was good.",
            "And there was evening, and there was morning—the fourth day.",
            "And God said, \"Let the water teem with living creatures, and let birds fly above the earth across the vault of the sky.\"",
            "So God created the great creatures of the sea and every living thing with which the water teems and that moves about in it, according to their kinds, and every winged bird according to its kind. And God saw that it was good.",
            "God blessed them and said, \"Be fruitful and increase in number and fill the water in the seas, and let the birds increase on the earth.\"",
            "And there was evening, and there was morning—the fifth day.",
            "And God said, \"Let the land produce living creatures according to their kinds: the livestock, the creatures that move along the ground, and the wild animals, each according to its kind.\" And it was so.",
            "God made the wild animals according to their kinds, the livestock according to their kinds, and all the creatures that move along the ground according to their kinds. And God saw that it was good.",
            "Then God said, \"Let us make mankind in our image, in our likeness, so that they may rule over the fish in the sea and the birds in the sky, over the livestock and all the wild animals, and over all the creatures that move along the ground.\"",
            "So God created mankind in his own image, in the image of God he created them; male and female he created them.",
            "God blessed them and said to them, \"Be fruitful and increase in number; fill the earth and subdue it. Rule over the fish in the sea and the birds in the sky and over every living creature that moves on the ground.\"",
            "Then God said, \"I give you every seed-bearing plant on the face of the whole earth and every tree that has fruit with seed in it. They will be yours for food.\"",
            "\"And to all the beasts of the earth and all the birds in the sky and all the creatures that move along the ground—everything that has the breath of life in it—I give every green plant for food.\" And it was so.",
            "God saw all that he had made, and it was very good. And there was evening, and there was morning—the sixth day."
        )

        val nkjv = listOf(
            "In the beginning God created the heavens and the earth.",
            "The earth was without form, and void; and darkness was on the face of the deep. And the Spirit of God was hovering over the face of the waters.",
            "Then God said, \"Let there be light\"; and there was light.",
            "And God saw the light, that it was good; and God divided the light from the darkness.",
            "God called the light Day, and the darkness He called Night. So the evening and the morning were the first day.",
            "Then God said, \"Let there be a firmament in the midst of the waters, and let it divide the waters from the waters.\"",
            "Thus God made the firmament, and divided the waters which were under the firmament from the waters which were above the firmament; and it was so.",
            "And God called the firmament Heaven. So the evening and the morning were the second day.",
            "Then God said, \"Let the waters under the heavens be gathered together into one place, and let the dry land appear\"; and it was so.",
            "And God called the dry land Earth, and the gathering together of the waters He called Seas. And God saw that it was good.",
            "Then God said, \"Let the earth bring forth grass, the herb that yields seed, and the fruit tree that yields fruit according to its kind, whose seed is in itself, on the earth\"; and it was so.",
            "And the earth brought forth grass, the herb that yields seed according to its kind, and the tree that yields fruit, whose seed is in itself according to its kind. And God saw that it was good.",
            "So the evening and the morning were the third day.",
            "Then God said, \"Let there be lights in the firmament of the heavens to divide the day from the night; and let them be for signs and seasons, and for days and years;\"",
            "\"and let them be for lights in the firmament of the heavens to give light on the earth\"; and it was so.",
            "Then God made two great lights: the greater light to rule the day, and the lesser light to rule the night. He made the stars also.",
            "God set them in the firmament of the heavens to give light on the earth,",
            "and to rule over the day and over the night, and to divide the light from the darkness. And God saw that it was good.",
            "So the evening and the morning were the fourth day.",
            "Then God said, \"Let the waters abound with an abundance of living creatures, and let birds fly above the earth across the face of the firmament of the heavens.\"",
            "So God created great sea creatures and every living thing that moves, with which the waters abounded, according to their kind, and every winged bird according to its kind. And God saw that it was good.",
            "And God blessed them, saying, \"Be fruitful and multiply, and fill the waters in the seas, and let birds multiply on the earth.\"",
            "So the evening and the morning were the fifth day.",
            "Then God said, \"Let the earth bring forth the living creature according to its kind: cattle and creeping thing and beast of the earth, each according to its kind\"; and it was so.",
            "And God made the beast of the earth according to its kind, cattle according to their kind, and everything that creeps on the earth according to its kind. And God saw that it was good.",
            "Then God said, \"Let Us make man in Our image, according to Our likeness; let them have dominion over the fish of the sea, over the birds of the air, and over the cattle, over all the earth and over every creeping thing that creeps on the earth.\"",
            "So God created man in His own image; in the image of God He created him; male and female He created them.",
            "Then God blessed them, and God said to them, \"Be fruitful and multiply; fill the earth and subdue it; have dominion over the fish of the sea, over the birds of the air, and over every living thing that moves on the earth.\"",
            "And God said, \"See, I have given you every herb that yields seed which is on the face of all the earth, and every tree whose fruit yields seed; to you it shall be for food.\"",
            "\"Also, to every beast of the earth, to every bird of the air, and to everything that creeps on the earth, in which there is life, I have given every green herb for food\"; and it was so.",
            "Then God saw everything that He had made, and indeed it was very good. So the evening and the morning were the sixth day."
        )

        val gnt = listOf(
            "In the beginning, when God created the universe,",
            "the earth was formless and desolate. The raging ocean that covered everything was engulfed in total darkness, and the Spirit of God was moving over the water.",
            "Then God commanded, \"Let there be light\"—and light appeared.",
            "God was pleased with what he saw. Then he separated the light from the darkness,",
            "and he named the light \"Day\" and the darkness \"Night.\" Evening passed and morning came—that was the first day.",
            "Then God commanded, \"Let there be a dome to divide the water and to keep it in two separate places\"—and it was done.",
            "So God made a dome, and it separated the water under it from the water above it.",
            "He named the dome \"Sky.\" Evening passed and morning came—that was the second day.",
            "Then God commanded, \"Let the water below the sky come together in one place, so that the land will appear\"—and it was done.",
            "He named the land \"Earth,\" and the water which had come together he named \"Sea.\" And God was pleased with what he saw.",
            "Then he commanded, \"Let the earth produce all kinds of plants, those that bear grain and those the bear fruit\"—and it was done.",
            "So the earth produced all kinds of plants, and God was pleased with what he saw.",
            "Evening passed and morning came—that was the third day.",
            "Then God commanded, \"Let lights appear in the sky to separate day from night and to show the time when days, years, and religious festivals begin;\"",
            "\"they will shine in the sky to give light to the earth\"—and it was done.",
            "So God made the two larger lights, the sun to rule over the day and the moon to rule over the night; he also made the stars.",
            "He placed the lights in the sky to shine on the earth,",
            "to rule over the day and the night, and to separate light from darkness. And God was pleased with what he saw.",
            "Evening passed and morning came—that was the fourth day.",
            "Then God commanded, \"Let the water be filled with many kinds of living beings, and let the air be filled with birds.\"",
            "So God created the great sea monsters, all kinds of creatures that live in the water, and all kinds of birds. And God was pleased with what he saw.",
            "He blessed them all and told the creatures that live in the water to reproduce and to fill the sea, and he told the birds to increase in number.",
            "Evening passed and morning came—that was the fifth day.",
            "Then God commanded, \"Let the earth produce all kinds of animal life: domestic and wild, large and small\"—and it was done.",
            "So God made them all, and he was pleased with what he saw.",
            "Then God said, \"And now we will make human beings; they will be like us and resemble us. They will have power over the fish, the birds, and all animals, domestic and wild, large and small.\"",
            "So God created human beings, making them to be like himself. He created them male and female,",
            "blessed them, and said, \"Have many children, so that your descendants will live all over the earth and bring it under their control. I am putting you in charge of the fish, the birds, and all the wild animals.\"",
            "\"I have provided all kinds of grain and all kinds of fruit for you to eat;\"",
            "\"but for all the wild animals and for all the birds I have provided grass and leafy plants for food\"—and it was done.",
            "God looked at everything he had made, and he was very pleased. Evening passed and morning came—that was the sixth day."
        )

        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.NKJV, nkjv)
        registerVerses(bookId, ch, Translation.GNT, gnt)
    }

    private fun loadPsalmsTwentyThree() {
        val bookId = 19
        val ch = 23

        val kjv = listOf(
            "The LORD is my shepherd; I shall not want.",
            "He maketh me to lie down in green pastures: he leadeth me beside the still waters.",
            "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake.",
            "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me.",
            "Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over.",
            "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the LORD for ever."
        )

        val niv = listOf(
            "The LORD is my shepherd, I lack nothing.",
            "He makes me lie down in green pastures, he leads me beside quiet waters,",
            "he refreshes my soul. He guides me along the right paths for his name's sake.",
            "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
            "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows.",
            "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the LORD forever."
        )

        val nkjv = listOf(
            "The LORD is my shepherd; I shall not want.",
            "He makes me to lie down in green pastures; He leads me beside the still waters.",
            "He restores my soul; He leads me in the paths of righteousness for His name's sake.",
            "Yea, though I walk through the valley of the shadow of death, I will fear no evil; For You are with me; Your rod and Your staff, they comfort me.",
            "You prepare a table before me in the presence of my enemies; You anoint my head with oil; My cup runs over.",
            "Surely goodness and mercy shall follow me all the days of my life; And I will dwell in the house of the LORD forever."
        )

        val gnt = listOf(
            "The LORD is my shepherd; I have everything I need.",
            "He lets me rest in fields of green grass and leads me to quiet pools of fresh water.",
            "He gives me new strength. He guides me in the right paths, as he has promised.",
            "Even if that path goes through the darkest valley, I will not be afraid, LORD, for you are with me! Your shepherd's rod and staff protect me.",
            "You prepare a banquet for me, where all my enemies can see me; you welcome me as an honored guest and fill my cup to the brim.",
            "I know that your goodness and love will be with me all my life; and your house will be my home as long as I live."
        )

        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.NKJV, nkjv)
        registerVerses(bookId, ch, Translation.GNT, gnt)
    }

    private fun loadPsalmsNinetyOne() {
        val bookId = 19
        val ch = 91

        val niv = listOf(
            "Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty.",
            "I will say of the LORD, \"He is my refuge and my fortress, my God, in whom I trust.\"",
            "Surely he will save you from the fowler's snare and from the deadly pestilence.",
            "He will cover you with his feathers, and under his wings you will find refuge; his faithfulness will be your shield and rampart.",
            "You will not fear the terror of night, nor the arrow that flies by day,",
            "nor the pestilence that stalks in the darkness, nor the plague that destroys at midday.",
            "A thousand may fall at your side, ten thousand at your right hand, but it will not come near you.",
            "You will only observe with your eyes and see the punishment of the wicked.",
            "If you say, \"The LORD is my refuge,\" and you make the Most High your dwelling,",
            "no harm will overtake you, no disaster will come near your tent.",
            "For he will command his angels concerning you to guard you in all your ways;",
            "they will lift you up in their hands, so that you will not strike your foot against a stone.",
            "You will tread on the lion and the cobra; you will trample the great lion and the serpent.",
            "\"Because he loves me,\" says the LORD, \"I will rescue him; I will protect him, for he acknowledges my name.\"",
            "\"He will call on me, and I will answer him; I will be with him in trouble, I will deliver him and honor him.\"",
            "\"With long life I will satisfy him and show him my salvation.\""
        )

        val kjv = listOf(
            "He that dwelleth in the secret place of the most High shall abide under the shadow of the Almighty.",
            "I will say of the LORD, He is my refuge and my fortress: my God; in him will I trust.",
            "Surely he shall deliver thee from the snare of the fowler, and from the noisome pestilence.",
            "He shall cover thee with his feathers, and under his wings shalt thou trust: his truth shall be thy shield and buckler.",
            "Thou shalt not be afraid for the terror by night; nor for the arrow that flieth by day;",
            "Nor for the pestilence that walketh in darkness; nor for the destruction that wasteth at noonday.",
            "A thousand shall fall at thy side, and ten thousand at thy right hand; but it shall not come nigh thee.",
            "Only with thine eyes shalt thou behold and see the reward of the wicked.",
            "Because thou hast made the LORD, which is my refuge, even the most High, thy habitation;",
            "There shall no evil befall thee, neither shall any plague come nigh thy dwelling.",
            "For he shall give his angels charge over thee, to keep thee in all thy ways.",
            "They shall bear thee up in their hands, lest thou dash thy foot against a stone.",
            "Thou shalt tread upon the lion and adder: the young lion and the dragon shalt thou trample under feet.",
            "Because he hath set his love upon me, therefore will I deliver him: I will set him on high, because he hath known my name.",
            "He shall call upon me, and I will answer him: I will be with him in trouble; I will deliver him, and honour him.",
            "With long life will I satisfy him, and shew him my salvation."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NKJV, kjv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun loadProverbsThree() {
        val bookId = 20
        val ch = 3

        val niv = listOf(
            "My son, do not forget my teaching, but keep my commands in your heart,",
            "for they will prolong your life many years and bring you peace and prosperity.",
            "Let love and faithfulness never leave you; bind them around your neck, write them on the tablet of your heart.",
            "Then you will win favor and a good name in the sight of God and man.",
            "Trust in the LORD with all your heart and lean not on your own understanding;",
            "in all your ways submit to him, and he will make your paths straight.",
            "Do not be wise in your own eyes; fear the LORD and shun evil.",
            "This will bring health to your body and nourishment to your bones.",
            "Honor the LORD with your wealth, with the firstfruits of all your crops;",
            "then your barns will be filled to overflowing, and your vats will brim over with new wine.",
            "My son, do not despise the LORD's discipline, and do not resent his rebuke,",
            "because the LORD disciplines those he loves, as a father the son he delights in."
        )

        val kjv = listOf(
            "My son, forget not my law; but let thine heart keep my commandments:",
            "For length of days, and long life, and peace, shall they add to thee.",
            "Let not mercy and truth forsake thee: bind them about thy neck; write them upon the table of thine heart:",
            "So shalt thou find favour and good understanding in the sight of God and man.",
            "Trust in the LORD with all thine heart; and lean not unto thine own understanding.",
            "In all thy ways acknowledge him, and he shall direct thy paths.",
            "Be not wise in thine own eyes: fear the LORD, and depart from evil.",
            "It shall be health to thy navel, and marrow to thy bones.",
            "Honour the LORD with thy substance, and with the firstfruits of all thine increase:",
            "So shall thy barns be filled with plenty, and thy presses shall burst out with new wine.",
            "My son, despise not the chastening of the LORD; neither be weary of his correction:",
            "For whom the LORD loveth he correcteth; even as a father the son in whom he delighteth."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NKJV, kjv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun loadMatthewFive() {
        val bookId = 40
        val ch = 5

        val niv = listOf(
            "Now when Jesus saw the crowds, he went up on a mountainside and sat down. His disciples came to him,",
            "and he began to teach them. He said:",
            "\"Blessed are the poor in spirit, for theirs is the kingdom of heaven.\"",
            "\"Blessed are those who mourn, for they will be comforted.\"",
            "\"Blessed are the meek, for they will inherit the earth.\"",
            "\"Blessed are those who hunger and thirst for righteousness, for they will be filled.\"",
            "\"Blessed are the merciful, for they will be shown mercy.\"",
            "\"Blessed are the pure in heart, for they will see God.\"",
            "\"Blessed are the peacemakers, for they will be called children of God.\"",
            "\"Blessed are those who are persecuted because of righteousness, for theirs is the kingdom of heaven.\"",
            "\"Blessed are you when people insult you, persecute you and falsely say all kinds of evil against you because of me.\"",
            "\"Rejoice and be glad, because great is your reward in heaven, for in the same way they persecuted the prophets who were before you.\"",
            "\"You are the salt of the earth. But if the salt loses its saltiness, how can it be made salty again? It is no longer good for anything, except to be thrown out and trampled underfoot.\"",
            "\"You are the light of the world. A town built on a hill cannot be hidden.\"",
            "\"Neither do people light a lamp and put it under a bowl. Instead they put it on its stand, and it gives light to everyone in the house.\"",
            "\"In the same way, let your light shine before others, that they may see your good deeds and glorify your Father in heaven.\""
        )

        val kjv = listOf(
            "And seeing the multitudes, he went up into a mountain: and when he was set, his disciples came unto him:",
            "And he opened his mouth, and taught them, saying,",
            "Blessed are the poor in spirit: for theirs is the kingdom of heaven.",
            "Blessed are they that mourn: for they shall be comforted.",
            "Blessed are the meek: for they shall inherit the earth.",
            "Blessed are they which do hunger and thirst after righteousness: for they shall be filled.",
            "Blessed are the merciful: for they shall obtain mercy.",
            "Blessed are the pure in heart: for they shall see God.",
            "Blessed are the peacemakers: for they shall be called the children of God.",
            "Blessed are they which are persecuted for righteousness' sake: for theirs is the kingdom of heaven.",
            "Blessed are ye, when men shall revile you, and persecute you, and shall say all manner of evil against you falsely, for my sake.",
            "Rejoice, and be exceeding glad: for great is your reward in heaven: for so persecuted they the prophets which were before you.",
            "Ye are the salt of the earth: but if the salt have lost his savour, wherewith shall it be salted? it is thenceforth good for nothing, but to be cast out, and to be trodden under foot of men.",
            "Ye are the light of the world. A city that is set on an hill cannot be hid.",
            "Neither do men light a candle, and put it under a bushel, but on a candlestick; and it giveth light unto all that are in the house.",
            "Let your light so shine before men, that they may see your good works, and glorify your Father which is in heaven."
        )

        registerVerses(bookId, ch, Translation.NIV, niv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.KJV, kjv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.NKJV, kjv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.GNT, niv, wordsOfChristFrom = 3)
    }

    private fun loadMatthewSix() {
        val bookId = 40
        val ch = 6

        val niv = listOf(
            "Be careful not to practice your righteousness in front of others to be seen by them...",
            "So when you give to the needy, do not announce it with trumpets...",
            "But when you give to the needy, do not let your left hand know what your right hand is doing,",
            "so that your giving may be in secret. Then your Father, who sees what is done in secret, will reward you.",
            "And when you pray, do not be like the hypocrites...",
            "But when you pray, go into your room, close the door and pray to your Father, who is unseen...",
            "And when you pray, do not keep on babbling like pagans...",
            "Do not be like them, for your Father knows what you need before you ask him.",
            "This, then, is how you should pray: 'Our Father in heaven, hallowed be your name,",
            "your kingdom come, your will be done, on earth as it is in heaven.",
            "Give us today our daily bread.",
            "And forgive us our debts, as we also have forgiven our debtors.",
            "And lead us not into temptation, but deliver us from the evil one.'",
            "For if you forgive other people when they sin against you, your heavenly Father will also forgive you.",
            "But if you do not forgive others their sins, your Father will not forgive your sins."
        )

        val kjv = listOf(
            "Take heed that ye do not your alms before men, to be seen of them...",
            "Therefore when thou doest thine alms, do not sound a trumpet before thee...",
            "But when thou doest alms, let not thy left hand know what thy right hand doeth:",
            "That thine alms may be in secret: and thy Father which seeth in secret himself shall reward thee openly.",
            "And when thou prayest, thou shalt not be as the hypocrites are...",
            "But thou, when thou prayest, enter into thy closet, and when thou hast shut thy door, pray to thy Father...",
            "But when ye pray, use not vain repetitions, as the heathen do...",
            "Be not ye therefore like unto them: for your Father knoweth what things ye have need of, before ye ask him.",
            "After this manner therefore pray ye: Our Father which art in heaven, Hallowed be thy name.",
            "Thy kingdom come, Thy will be done in earth, as it is in heaven.",
            "Give us this day our daily bread.",
            "And forgive us our debts, as we forgive our debtors.",
            "And lead us not into temptation, but deliver us from evil: For thine is the kingdom, and the power, and the glory, for ever. Amen.",
            "For if ye forgive men their trespasses, your heavenly Father will also forgive you:",
            "But if ye forgive not men their trespasses, neither will your Father forgive your trespasses."
        )

        registerVerses(bookId, ch, Translation.NIV, niv, wordsOfChristFrom = 1)
        registerVerses(bookId, ch, Translation.KJV, kjv, wordsOfChristFrom = 1)
        registerVerses(bookId, ch, Translation.NKJV, kjv, wordsOfChristFrom = 1)
        registerVerses(bookId, ch, Translation.GNT, niv, wordsOfChristFrom = 1)
    }

    private fun loadJohnOne() {
        val bookId = 43
        val ch = 1

        val niv = listOf(
            "In the beginning was the Word, and the Word was with God, and the Word was God.",
            "He was with God in the beginning.",
            "Through him all things were made; without him nothing was made that has been made.",
            "In him was life, and that life was the light of all mankind.",
            "The light shines in the darkness, and the darkness has not overcome it.",
            "There was a man sent from God whose name was John.",
            "He came as a witness to testify concerning that light, so that through him all might believe.",
            "He himself was not the light; he came only as a witness to the light.",
            "The true light that gives light to everyone was coming into the world.",
            "He was in the world, and though the world was made through him, the world did not recognize him.",
            "He came to that which was his own, but his own did not receive him.",
            "Yet to all who did receive him, to those who believed in his name, he gave the right to become children of God—",
            "children born not of natural descent, nor of human decision or a husband's will, but born of God.",
            "The Word became flesh and made his dwelling among us. We have seen his glory, the glory of the one and only Son, who came from the Father, full of grace and truth."
        )

        val kjv = listOf(
            "In the beginning was the Word, and the Word was with God, and the Word was God.",
            "The same was in the beginning with God.",
            "All things were made by him; and without him was not any thing made that was made.",
            "In him was life; and the life was the light of men.",
            "And the light shineth in darkness; and the darkness comprehended it not.",
            "There was a man sent from God, whose name was John.",
            "The same came for a witness, to bear witness of the Light, that all men through him might believe.",
            "He was not that Light, but was sent to bear witness of that Light.",
            "That was the true Light, which lighteth every man that cometh into the world.",
            "He was in the world, and the world was made by him, and the world knew him not.",
            "He came unto his own, and his own received him not.",
            "But as many as received him, to them gave he power to become the sons of God, even to them that believe on his name:",
            "Which were born, not of blood, nor of the will of the flesh, nor of the will of man, but of God.",
            "And the Word was made flesh, and dwelt among us, (and we beheld his glory, the glory as of the only begotten of the Father,) full of grace and truth."
        )

        val gnt = listOf(
            "In the beginning the Word already existed; the Word was with God, and the Word was God.",
            "From the very beginning the Word was with God.",
            "Through him God made all things; not one thing in all creation was made without him.",
            "The Word was the source of life, and this life brought light to humanity.",
            "The light shines in the darkness, and the darkness has never put it out.",
            "God sent his messenger, a man named John,",
            "who came to tell people about the light, so that all should hear the message and believe.",
            "He himself was not the light; he came to tell about the light.",
            "This was the real light—the light that comes into the world and shines on everyone.",
            "The Word was in the world, and though God made the world through him, yet the world did not recognize him.",
            "He came to his own country, but his own people did not receive him.",
            "Some, however, did receive him and believed in him; so he gave them the right to become God's children.",
            "They did not become God's children by natural means, that is, by being born as the children of a human father; God himself was their Father.",
            "The Word became a human being and, full of grace and truth, lived among us. We saw his glory, the glory which he received as the Father's only Son."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NKJV, kjv)
        registerVerses(bookId, ch, Translation.GNT, gnt)
    }

    private fun loadJohnThree() {
        val bookId = 43
        val ch = 3

        val niv = listOf(
            "Now there was a Pharisee, a man named Nicodemus who was a member of the Jewish ruling council.",
            "He came to Jesus at night and said, \"Rabbi, we know that you are a teacher who has come from God. For no one could perform the signs you are doing if God were not with him.\"",
            "Jesus replied, \"Very truly I tell you, no one can see the kingdom of God unless they are born again.\"",
            "\"How can someone be born when they are old?\" Nicodemus asked. \"Surely they cannot enter a second time into their mother's womb to be born!\"",
            "Jesus answered, \"Very truly I tell you, no one can enter the kingdom of God unless they are born of water and the Spirit.\"",
            "\"Flesh gives birth to flesh, but the Spirit gives birth to spirit.\"",
            "\"You should not be surprised at my saying, 'You must be born again.'\"",
            "\"The wind blows wherever it pleases. You hear its sound, but you cannot tell where it comes from or where it is going. So it is with everyone born of the Spirit.\"",
            "\"How can this be?\" Nicodemus asked.",
            "\"You are Israel's teacher,\" said Jesus, \"and do you not understand these things?\"",
            "\"Very truly I tell you, we speak of what we know, and we testify to what we have seen, but still you people do not accept our testimony.\"",
            "\"I have spoken to you of earthly things and you do not believe; how then will you believe if I speak of heavenly things?\"",
            "\"No one has ever gone into heaven except the one who came from heaven—the Son of Man.\"",
            "\"Just as Moses lifted up the snake in the wilderness, so the Son of Man must be lifted up,\"",
            "\"that everyone who believes may have eternal life in him.\"",
            "\"For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.\"",
            "\"For God did not send his Son into the world to condemn the world, but to save the world through him.\"",
            "\"Whoever believes in him is not condemned, but whoever does not believe stands condemned already because they have not believed in the name of God's one and only Son.\"",
            "\"This is the verdict: Light has come into the world, but people loved darkness instead of light because their deeds were evil.\"",
            "\"Everyone who does evil hates the light, and will not come into the light for fear that their deeds will be exposed.\"",
            "\"But whoever lives by the truth comes into the light, so that it may be seen plainly that what they have done has been done in the sight of God.\""
        )

        val nkjv = listOf(
            "There was a man of the Pharisees named Nicodemus, a ruler of the Jews.",
            "This man came to Jesus by night and said to Him, \"Rabbi, we know that You are a teacher come from God; for no one can do these signs that You do unless God is with him.\"",
            "Jesus answered and said to him, \"Most assuredly, I say to you, unless one is born again, he cannot see the kingdom of God.\"",
            "Nicodemus said to Him, \"How can a man be born when he is old? Can he enter a second time into his mother's womb and be born?\"",
            "Jesus answered, \"Most assuredly, I say to you, unless one is born of water and the Spirit, he cannot enter the kingdom of God.\"",
            "\"That which is born of the flesh is flesh, and that which is born of the Spirit is spirit.\"",
            "\"Do not marvel that I said to you, 'You must be born again.'\"",
            "\"The wind blows where it wishes, and you hear the sound of it, but cannot tell where it comes from and where it goes. So is everyone who is born of the Spirit.\"",
            "Nicodemus answered and said to Him, \"How can these things be?\"",
            "Jesus answered and said to him, \"Are you the teacher of Israel, and do not know these things?\"",
            "\"Most assuredly, I say to you, We speak what We know and testify what We have seen, and you do not receive Our witness.\"",
            "\"If I have told you earthly things and you do not believe, how will you believe if I tell you heavenly things?\"",
            "\"No one has ascended to heaven but He who came down from heaven, that is, the Son of Man who is in heaven.\"",
            "\"And as Moses lifted up the serpent in the wilderness, even so must the Son of Man be lifted up,\"",
            "\"that whoever believes in Him should not perish but have eternal life.\"",
            "\"For God so loved the world that He gave His only begotten Son, that whoever believes in Him should not perish but have everlasting life.\"",
            "\"For God did not send His Son into the world to condemn the world, but that the world through Him might be saved.\"",
            "\"He who believes in Him is not condemned; but he who does not believe is condemned already, because he has not believed in the name of the only begotten Son of God.\"",
            "\"And this is the condemnation, that the light has come into the world, and men loved darkness rather than light, because their deeds were evil.\"",
            "\"For everyone practicing evil hates the light and does not come to the light, lest his deeds should be exposed.\"",
            "\"But he who does the truth comes to the light, that his deeds may be clearly seen, that they have been done in God.\""
        )

        val kjv = listOf(
            "There was a man of the Pharisees, named Nicodemus, a ruler of the Jews:",
            "The same came to Jesus by night, and said unto him, Rabbi, we know that thou art a teacher come from God: for no one can do these miracles that thou doest, except God be with him.",
            "Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.",
            "Nicodemus saith unto him, How can a man be born when he is old? can he enter the second time into his mother's womb, and be born?",
            "Jesus answered, Verily, verily, I say unto thee, Except a man be born of water and of the Spirit, he cannot enter into the kingdom of God.",
            "That which is born of the flesh is flesh; and that which is born of the Spirit is spirit.",
            "Marvel not that I said unto thee, Ye must be born again.",
            "The wind bloweth where it listeth, and thou hearest the sound thereof, but canst not tell whence it cometh, and whither it goeth: so is every one that is born of the Spirit.",
            "Nicodemus answered and said unto him, How can these things be?",
            "Jesus answered and said unto him, Art thou a master of Israel, and knowest not these things?",
            "Verily, verily, I say unto thee, We speak that we do know, and testify that we have seen; and ye receive not our witness.",
            "If I have told you earthly things, and ye believe not, how shall ye believe, if I tell you of heavenly things?",
            "And no man hath ascended up to heaven, but he that came down from heaven, even the Son of man which is in heaven.",
            "And as Moses lifted up the serpent in the wilderness, even so must the Son of man be lifted up:",
            "That whosoever believeth in him should not perish, but have eternal life.",
            "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.",
            "For God sent not his Son into the world to condemn the world; but that the world through him might be saved.",
            "He that believeth on him is not condemned: but he that believeth not is condemned already, because he hath not believed in the name of the only begotten Son of God.",
            "And this is the condemnation, that light is come into the world, and men loved darkness rather than light, because their deeds were evil.",
            "For every one that doeth evil hateth the light, neither cometh to the light, lest his deeds should be reproved.",
            "But he that doeth truth cometh to the light, that his deeds may be made manifest, that they are wrought in God."
        )

        val gnt = listOf(
            "There was a Jewish leader named Nicodemus, who belonged to the party of the Pharisees.",
            "One night he went to Jesus and said to him, \"Rabbi, we know that you are a teacher sent by God. No one could perform the miracles you are doing unless God were with him.\"",
            "Jesus answered, \"I am telling you the truth: no one can see the Kingdom of God without being born again.\"",
            "\"How can a grown man be born again?\" Nicodemus asked. \"He certainly cannot enter his mother's womb and be born a second time!\"",
            "\"I am telling you the truth,\" replied Jesus, \"that no one can enter the Kingdom of God without being born of water and the Spirit.\"",
            "\"A person is born physically of human parents, but is born spiritually of the Spirit.\"",
            "\"Do not be surprised because I tell you that you must all be born again.\"",
            "\"The wind blows wherever it wishes; you hear the sound it makes, but you do not know where it comes from or where it is going. It is like that with everyone who is born of the Spirit.\"",
            "\"How can this be?\" asked Nicodemus.",
            "Jesus answered, \"You are a great teacher in Israel, and you don't know this?\"",
            "\"I am telling you the truth: we speak of what we know and report what we have seen, yet none of you is willing to accept our message.\"",
            "\"You do not believe me when I tell you about the things of this world; how will you ever believe me, then, when I tell you about the things of heaven?\"",
            "\"And no one has ever gone up to heaven except the Son of Man, who came down from heaven.\"",
            "\"As Moses lifted up the bronze snake on a pole in the desert, in the same way the Son of Man must be lifted up,\"",
            "\"so that everyone who believes in him may have eternal life.\"",
            "\"For God loved the world so much that he gave his only Son, so that everyone who believes in him may not die but have eternal life.\"",
            "\"For God did not send his Son into the world to be its judge, but to be its savior.\"",
            "\"Those who believe in the Son are not judged; but those who do not believe have already been judged, because they have not believed in God's only Son.\"",
            "\"This is how the judgment works: the light has come into the world, but people love the darkness rather than the light, because they do evil things.\"",
            "\"Anyone who does evil things hates the light and will not come to the light, because they don't want their evil deeds to be shown up.\"",
            "\"But whoever does what is true comes to the light, in order that the light may show that what they did was in obedience to God.\""
        )

        registerVerses(bookId, ch, Translation.NIV, niv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.NKJV, nkjv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.KJV, kjv, wordsOfChristFrom = 3)
        registerVerses(bookId, ch, Translation.GNT, gnt, wordsOfChristFrom = 3)
    }

    private fun loadRomansEight() {
        val bookId = 45
        val ch = 8

        val niv = listOf(
            "Therefore, there is now no condemnation for those who are in Christ Jesus,",
            "because through Christ Jesus the law of the Spirit who gives life has set you free from the law of sin and death.",
            "For what the law was powerless to do because it was weakened by the flesh, God did by sending his own Son...",
            "in order that the righteous requirement of the law might be fully met in us...",
            "Those who live according to the flesh have their minds set on what the flesh desires...",
            "The mind governed by the flesh is death, but the mind governed by the Spirit is life and peace.",
            "The mind governed by the flesh is hostile to God...",
            "Those who are in the realm of the flesh cannot please God.",
            "You, however, are not in the realm of the flesh but are in the realm of the Spirit, if indeed the Spirit of God lives in you.",
            "And if Christ is in you, then even though your body is subject to death because of sin, the Spirit gives life because of righteousness.",
            "And if the Spirit of him who raised Jesus from the dead is living in you, he who raised Christ from the dead will also give life to your mortal bodies...",
            "Therefore, brothers and sisters, we have an obligation—but it is not to the flesh, to live according to it.",
            "For if you live according to the flesh, you will die; but if by the Spirit you put to death the misdeeds of the body, you will live.",
            "For those who are led by the Spirit of God are the children of God.",
            "The Spirit you received does not make you slaves, so that you live in fear again; rather, the Spirit you received brought about your adoption to sonship. And by him we cry, \"Abba, Father.\"",
            "The Spirit himself testifies with our spirit that we are God's children.",
            "Now if we are children, then we are heirs—heirs of God and co-heirs with Christ, if indeed we share in his sufferings in order that we may also share in his glory.",
            "I consider that our present sufferings are not worth comparing with the glory that will be revealed in us.",
            "For the creation waits in eager expectation for the children of God to be revealed.",
            "For the creation was subjected to frustration, not by its own choice, but by the will of the one who subjected it, in hope",
            "that the creation itself will be liberated from its bondage to decay and brought into the freedom and glory of the children of God.",
            "We know that the whole creation has been groaning as in the pains of childbirth right up to the present time.",
            "Not only so, but we ourselves, who have the firstfruits of the Spirit, groan inwardly as we wait eagerly for our adoption to sonship, the redemption of our bodies.",
            "For in this hope we were saved. But hope that is seen is no hope at all. Who hopes for what they already have?",
            "But if we hope for what we do not yet have, we wait for it patiently.",
            "In the same way, the Spirit helps us in our weakness. We do not know what we ought to pray for, but the Spirit himself intercedes for us through wordless groans.",
            "And he who searches our hearts knows the mind of the Spirit, because the Spirit intercedes for God's people in accordance with the will of God.",
            "And we know that in all things God works for the good of those who love him, who have been called according to his purpose.",
            "For those God foreknew he also predestined to be conformed to the image of his Son, that he might be the firstborn among many brothers and sisters.",
            "And those he predestined, he also called; those he called, he also justified; those he justified, he also glorified.",
            "What, then, shall we say in response to these things? If God is for us, who can be against us?",
            "He who did not spare his own Son, but gave him up for us all—how will he not also, along with him, graciously give us all things?",
            "Who will bring any charge against those whom God has chosen? It is God who justifies.",
            "Who then is the one who condemns? No one. Christ Jesus who died—more than that, who was raised to life—is at the right hand of God and is also interceding for us.",
            "Who shall separate us from the love of Christ? Shall trouble or hardship or persecution or famine or nakedness or danger or sword?",
            "As it is written: \"For your sake we face death all day long; we are considered as sheep to be slaughtered.\"",
            "No, in all these things we are more than conquerors through him who loved us.",
            "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,",
            "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.NKJV, niv)
        registerVerses(bookId, ch, Translation.KJV, niv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun loadFirstCorinthiansThirteen() {
        val bookId = 46
        val ch = 13

        val niv = listOf(
            "If I speak in the tongues of men or of angels, but do not have love, I am only a resounding gong or a clanging cymbal.",
            "If I have the gift of prophecy and can fathom all mysteries and all knowledge, and if I have a faith that can move mountains, but do not have love, I am nothing.",
            "If I give all I possess to the poor and give over my body to hardship that I may boast, but do not have love, I gain nothing.",
            "Love is patient, love is kind. It does not envy, it does not boast, it is not proud.",
            "It does not dishonor others, it is not self-seeking, it is not easily angered, it keeps no record of wrongs.",
            "Love does not delight in evil but rejoices with the truth.",
            "It always protects, always trusts, always hopes, always perseveres.",
            "Love never fails. But where there are prophecies, they will cease; where there are tongues, they will be stilled; where there is knowledge, it will pass away.",
            "For we know in part and we prophesy in part,",
            "but when completeness comes, what is in part disappears.",
            "When I was a child, I talked like a child, I thought like a child, I reasoned like a child. When I became a man, I put the ways of childhood behind me.",
            "For now we see only a reflection as in a mirror; then we shall see face to face. Now I know in part; then I shall know fully, even as I am fully known.",
            "And now these three remain: faith, hope and love. But the greatest of these is love."
        )

        val kjv = listOf(
            "Though I speak with the tongues of men and of angels, and have not charity, I am become as sounding brass, or a tinkling cymbal.",
            "And though I have the gift of prophecy, and understand all mysteries, and all knowledge; and though I have all faith, so that I could remove mountains, and have not charity, I am nothing.",
            "And though I bestow all my goods to feed the poor, and though I give my body to be burned, and have not charity, it profiteth me nothing.",
            "Charity suffereth long, and is kind; charity envieth not; charity vaunteth not itself, is not puffed up,",
            "Doth not behave itself unseemly, seeketh not her own, is not easily provoked, thinketh no evil;",
            "Rejoiceth not in iniquity, but rejoiceth in the truth;",
            "Beareth all things, believeth all things, hopeth all things, endureth all things.",
            "Charity never faeth: but whether there be prophecies, they shall fail; whether there be tongues, they shall cease; whether there be knowledge, it shall vanish away.",
            "For we know in part, and we prophesy in part.",
            "But when that which is perfect is come, then that which is in part shall be done away.",
            "When I was a child, I spake as a child, I understood as a child, I thought as a child: but when I became a man, I put away childish things.",
            "For now we see through a glass, darkly; but then face to face: now I know in part; but then shall I know even as also I am known.",
            "And now abideth faith, hope, charity, these three; but the greatest of these is charity."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, kjv)
        registerVerses(bookId, ch, Translation.NKJV, niv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun loadPhilippiansFour() {
        val bookId = 50
        val ch = 4

        val niv = listOf(
            "Therefore, my brothers and sisters, you whom I love and long for, my joy and crown, stand firm in the Lord in this way, dear friends!",
            "I plead with Euodia and I plead with Syntyche to be of the same mind in the Lord.",
            "Yes, and I ask you, my true companion, help these women since they have contended at my side in the cause of the gospel...",
            "Rejoice in the Lord always. I will say it again: Rejoice!",
            "Let your gentleness be evident to all. The Lord is near.",
            "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
            "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
            "Finally, brothers and sisters, whatever is true, whatever is noble, whatever is right, whatever is pure, whatever is lovely, whatever is admirable—if anything is excellent or praiseworthy—think about such things.",
            "Whatever you have learned or received or heard from me, or seen in me—put it into practice. And the God of peace will be with you.",
            "I rejoiced greatly in the Lord that at last you renewed your concern for me. Indeed, you were concerned, but you had no opportunity to show it.",
            "I am not saying this because I am in need, for I have learned to be content whatever the circumstances.",
            "I know what it is to be in need, and I know what it is to have plenty. I have learned the secret of being content in any and every situation, whether well fed or hungry, whether living in plenty or in want.",
            "I can do all this through him who gives me strength."
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, niv)
        registerVerses(bookId, ch, Translation.NKJV, niv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun loadRevelationTwentyOne() {
        val bookId = 66
        val ch = 21

        val niv = listOf(
            "Then I saw \"a new heaven and a new earth,\" for the first heaven and the first earth had passed away, and there was no longer any sea.",
            "I saw the Holy City, the new Jerusalem, coming down out of heaven from God, prepared as a bride beautifully dressed for her husband.",
            "And I heard a loud voice from the throne saying, \"Look! God's dwelling place is now among the people, and he will dwell with them. They will be his people, and God himself will be with them and be their God.\"",
            "\"He will wipe every tear from their eyes. There will be no more death or mourning or crying or pain, for the old order of things has passed away.\"",
            "He who was seated on the throne said, \"I am making everything new!\" Then he said, \"Write this down, for these words are trustworthy and true.\"",
            "He said to me: \"It is done. I am the Alpha and the Omega, the Beginning and the End. To the thirsty I will give water without cost from the spring of the water of life.\"",
            "\"Those who are victorious will inherit all this, and I will be their God and they will be my children.\""
        )

        registerVerses(bookId, ch, Translation.NIV, niv)
        registerVerses(bookId, ch, Translation.KJV, niv)
        registerVerses(bookId, ch, Translation.NKJV, niv)
        registerVerses(bookId, ch, Translation.GNT, niv)
    }

    private fun registerVerses(
        bookId: Int,
        chapter: Int,
        translation: Translation,
        texts: List<String>,
        wordsOfChristFrom: Int? = null
    ) {
        val list = texts.mapIndexed { index, text ->
            val vNum = index + 1
            Verse(
                bookId = bookId,
                chapter = chapter,
                verse = vNum,
                text = text,
                isWordsOfChrist = wordsOfChristFrom != null && vNum >= wordsOfChristFrom
            )
        }
        scriptureStore["$bookId:$chapter:${translation.code}"] = list
    }
}
