@file:Suppress("NonAsciiCharacters")

package csc.markobot.dsl

import csc.markobot.api.*


val defaultRobot = MakroBot("Wall-E",
        Head(Plastik(1), listOf(), Mouth(Speaker(1))),
        Body(Metal(1), listOf()),
        Hands(Plastik(1), LoadClass.Light, LoadClass.Medium),
        Chassis.Caterpillar(1)
)

val оченьЛегкая = LoadClass.VeryLight
val легкая = LoadClass.Light
val средняя = LoadClass.Medium
val тяжелая = LoadClass.Heavy
val оченьТяжелая = LoadClass.VeryHeavy
val огромная = LoadClass.Enormous



class MaterialBuilder {
    var thickness = 0

    fun buildPlastic() = Plastik(thickness)
    fun buildMetal() = Metal(thickness)
}


@MakroBotDsl
class EyeBuilder {
    var яркость = 0
    var количество = 0

    fun buildL(): Eye = LampEye(яркость)
    fun buildD(): Eye = LedEye(яркость)
}


@MakroBotDsl
class EyesBuilder {
    var eyes = mutableListOf<Eye>()
    private var lamps = mutableListOf<Eye>()
    private var leds = mutableListOf<Eye>()

    fun лампы(block: EyeBuilder.() -> Unit) {
        val eye = EyeBuilder().apply(block)
        repeat(eye.количество) {
            lamps.add(eye.buildL())
        }
        eyes.addAll(lamps)
    }
    fun диоды(block: EyeBuilder.() -> Unit) {
        val eye = EyeBuilder().apply(block)
        repeat(eye.количество) {
            leds.add(eye.buildD())
        }
        eyes.addAll(leds)
    }
}


@MakroBotDsl
class SpeakerBuilder {
    var мощность = 0

    fun build(): Speaker = Speaker(мощность)
}


@MakroBotDsl
class MouthBuilder {
    private var speaker: Speaker? = null

    fun динамик(block: SpeakerBuilder.() -> Unit) {
        speaker = SpeakerBuilder().apply(block).build()
    }

    fun build(): Mouth = Mouth(speaker)
}


@MakroBotDsl
class HeadBuilder {
    private var eyes = listOf<Eye>()
    private var mouth = defaultRobot.head.mouth
    private var material = defaultRobot.head.material
    var пластик = MaterialBuilder()
    var металл = MaterialBuilder()

    infix fun MaterialBuilder.толщиной(n: Int) {
        this.thickness = n
        material = if (this == пластик) this.buildPlastic()
        else this.buildMetal()
    }
    fun глаза(block: EyesBuilder.() -> Unit) {
        eyes = EyesBuilder().apply(block).eyes
    }
    fun рот(block: MouthBuilder.() -> Unit) {
        mouth = MouthBuilder().apply(block).build()
    }

    fun build(): Head = Head(material, eyes, mouth)
}


@MakroBotDsl
class StringsBuilder {
    val strings = mutableListOf<String>()

    operator fun String.unaryPlus() {
        strings.add(this)
    }
}


@MakroBotDsl
class BodyBuilder {
    private var strings = listOf<String>()
    private var material = defaultRobot.head.material
    var пластик = MaterialBuilder()
    var металл = MaterialBuilder()

    infix fun MaterialBuilder.толщиной(n: Int) {
        this.thickness = n
        material = if (this == пластик) this.buildPlastic()
        else this.buildMetal()
    }
    fun надпись(block: StringsBuilder.() -> Unit) {
        strings = StringsBuilder().apply(block).strings
    }

    fun build(): Body = Body(material, strings)
}


@MakroBotDsl
class HandsBuilder {
    var нагрузка: Pair<LoadClass, LoadClass> = Pair(defaultRobot.hands.minLoad, defaultRobot.hands.maxLoad)
    private var minLoad = нагрузка.first
    private var maxLoad = нагрузка.second

    private var material = defaultRobot.head.material
    var пластик = MaterialBuilder()
    var металл = MaterialBuilder()

    infix fun MaterialBuilder.толщиной(n: Int) {
        this.thickness = n
        material = if (this == пластик) this.buildPlastic()
        else this.buildMetal()
    }
    operator fun LoadClass.minus(max: LoadClass): Pair<LoadClass, LoadClass> {
        return Pair(this, max)
    }

    fun build(): Hands = Hands(material, minLoad, maxLoad)
}


@MakroBotDsl
class ChassisBuilder {
    var width = 0
    var диаметр = 0
    var количество = 0

    fun buildCaterpillar() = Chassis.Caterpillar(width)
    fun buildWheel() = Chassis.Wheel(количество, диаметр)
}


@MakroBotDsl
class RobotBuilder (private val name: String) {
    private var head = defaultRobot.head
    private var body = defaultRobot.body
    private var hands = defaultRobot.hands

    var шасси = defaultRobot.chassis
    var гусеницы = ChassisBuilder()
    var ноги = Chassis.Legs

    infix fun ChassisBuilder.шириной(n: Int): Chassis {
        this.width = n
        return this.buildCaterpillar()
    }
    fun колеса(block: ChassisBuilder.() -> Unit): Chassis {
        return ChassisBuilder().apply(block).buildWheel()
    }
    fun голова(block: HeadBuilder.() -> Unit) {
        head = HeadBuilder().apply(block).build()
    }
    fun туловище(block: BodyBuilder.() -> Unit) {
        body = BodyBuilder().apply(block).build()
    }
    fun руки(block: HandsBuilder.() -> Unit) {
        hands = HandsBuilder().apply(block).build()
    }

    fun build(): MakroBot = MakroBot(name, head, body, hands, шасси)
}


fun робот(name: String, block: RobotBuilder.() -> Unit): MakroBot = RobotBuilder(name).apply(block).build()



