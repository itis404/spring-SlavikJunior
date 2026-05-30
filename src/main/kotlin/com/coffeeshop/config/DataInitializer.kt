package com.coffeeshop.config

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.contracts.ModifierCategory
import com.coffeeshop.entity.*
import com.coffeeshop.repository.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * Seeds the database with menu data and a default admin user in dev mode.
 * Data mirrors docs/СВОДНОЕ МЕНЮ ДВОЙКА - Лист1.csv
 */
@Component
@Profile("dev")
class DataInitializer(
    private val menuItemRepository: MenuItemRepository,
    private val menuItemVolumeRepository: MenuItemVolumeRepository,
    private val modifierRepository: ModifierRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (menuItemRepository.count() > 0) {
            log.info("DataInitializer: data already seeded, skipping")
            return
        }

        log.info("DataInitializer: seeding menu data...")
        val modifiers = seedModifiers()
        seedMenuItems(modifiers)
        seedAdminUser()
        log.info("DataInitializer: done")
    }

    private fun seedModifiers(): ModifierMap {
        fun mod(name: String, category: ModifierCategory, price: Int, photoUrl: String? = null) =
            modifierRepository.save(Modifier(name = name, category = category, price = BigDecimal(price), photoUrl = photoUrl))

        val syrups = listOf(
            mod("Груша, корица, ваниль", ModifierCategory.SYRUP, 50),
            mod("Вишня, малина, бобы тонка", ModifierCategory.SYRUP, 50),
            mod("Вишня, вино, шоколад", ModifierCategory.SYRUP, 50),
            mod("Трюфель", ModifierCategory.SYRUP, 50),
            mod("Земляника с цитрусовым перцем", ModifierCategory.SYRUP, 50),
            mod("Печёное яблоко и лайм", ModifierCategory.SYRUP, 50),
            mod("Абрикос, морковь и мускатный орех", ModifierCategory.SYRUP, 50),
        )
        val marshmallow = listOf(
            mod("Маршмеллоу", ModifierCategory.MARSHMALLOW, 50, "/menu/modifiers/маршмеллоу.png"),
        )
        val altMilk = listOf(
            mod("Кокосовое молоко", ModifierCategory.ALT_MILK, 100, "/menu/modifiers/кокосовое молоко.png"),
            mod("Миндальное молоко", ModifierCategory.ALT_MILK, 100, "/menu/modifiers/миндальное молоко.png"),
            mod("Банановое молоко", ModifierCategory.ALT_MILK, 100, "/menu/modifiers/банановое молоко.png"),
            mod("Безлактозное молоко", ModifierCategory.ALT_MILK, 100, "/menu/modifiers/безлактозное молоко.png"),
            mod("Протеиновое молоко", ModifierCategory.ALT_MILK, 100, "/menu/modifiers/протеиновое молоко.png"),
        )
        val vitaminShots = listOf(
            mod("CBD", ModifierCategory.VITAMIN_SHOT, 100),
            mod("Морской коллаген", ModifierCategory.VITAMIN_SHOT, 100),
            mod("D3K2", ModifierCategory.VITAMIN_SHOT, 100),
            mod("B-комплекс", ModifierCategory.VITAMIN_SHOT, 100),
        )
        return ModifierMap(syrups, marshmallow, altMilk, vitaminShots)
    }

    private fun seedMenuItems(mods: ModifierMap) {
        fun  item(
            name: String,
            category: MenuCategory,
            description: String? = null,
            photoUrl: String? = null,
            volumes: List<Pair<Int, Int>>,
            syrup: Boolean = false,
            marshmallow: Boolean = false,
            altMilk: Boolean = false,
            vitaminShot: Boolean = false,
        ) {
            val menuItem = menuItemRepository.save(
                MenuItem(name = name, category = category, description = description, photoUrl = photoUrl),
            )
            volumes.forEach { (ml, price) ->
                menuItemVolumeRepository.save(
                    MenuItemVolume(menuItem = menuItem, volumeMl = ml, price = BigDecimal(price)),
                )
            }
            if (syrup) menuItem.compatibleModifiers.addAll(mods.syrups)
            if (marshmallow) menuItem.compatibleModifiers.addAll(mods.marshmallow)
            if (altMilk) menuItem.compatibleModifiers.addAll(mods.altMilk)
            if (vitaminShot) menuItem.compatibleModifiers.addAll(mods.vitaminShots)
        }

        // --- COFFEE ---
        item("Эспрессо", MenuCategory.COFFEE, photoUrl = "/menu/эспрессо.png",
            volumes = listOf(40 to 120), syrup = true, vitaminShot = true)
        item("Капучино", MenuCategory.COFFEE, photoUrl = "/menu/капучино.png",
            volumes = listOf(250 to 230, 350 to 260, 450 to 290), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Латте", MenuCategory.COFFEE, photoUrl = "/menu/латте.png",
            volumes = listOf(250 to 230, 350 to 260, 450 to 290), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Айс латте", MenuCategory.COFFEE, photoUrl = "/menu/айс латте.png",
            volumes = listOf(350 to 260, 450 to 290), syrup = true, altMilk = true, vitaminShot = true)
        item("Американо", MenuCategory.COFFEE, photoUrl = "/menu/американо.png",
            volumes = listOf(250 to 180, 350 to 210, 450 to 230), syrup = true, marshmallow = true, vitaminShot = true)
        item("Флэт уайт", MenuCategory.COFFEE, photoUrl = "/menu/флет.png",
            volumes = listOf(250 to 260), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Раф классик", MenuCategory.COFFEE, photoUrl = "/menu/раф классик.png",
            volumes = listOf(250 to 300, 350 to 330, 450 to 360), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Мокко", MenuCategory.COFFEE, photoUrl = "/menu/мокко.png",
            volumes = listOf(250 to 270, 350 to 300, 450 to 330), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Эспрессо-тоник", MenuCategory.COFFEE, photoUrl = "/menu/эспрессо-тоник.png",
            volumes = listOf(350 to 300, 450 to 330), syrup = true, vitaminShot = true)
        item("Бамбл", MenuCategory.COFFEE, photoUrl = "/menu/бамбл.png",
            volumes = listOf(350 to 350, 450 to 400), syrup = true, vitaminShot = true)

        // --- MATCHA ---
        item("Матча-латте", MenuCategory.MATCHA, photoUrl = "/menu/матча-латте.png",
            volumes = listOf(250 to 280, 350 to 310, 450 to 330), syrup = true, altMilk = true, vitaminShot = true)
        item("Айс матча-латте", MenuCategory.MATCHA, photoUrl = "/menu/айс матча.png",
            volumes = listOf(350 to 310, 450 to 330), syrup = true, altMilk = true, vitaminShot = true)
        item("Голубая матча", MenuCategory.MATCHA, photoUrl = "/menu/голубая матча.png",
            volumes = listOf(250 to 280, 350 to 310, 450 to 330), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Матча тоник", MenuCategory.MATCHA, photoUrl = "/menu/матча тоник.png",
            volumes = listOf(350 to 310, 450 to 350), syrup = true, vitaminShot = true)
        item("Матча бамбл", MenuCategory.MATCHA, photoUrl = "/menu/матча бамбл.png",
            volumes = listOf(350 to 310, 450 to 350), syrup = true, vitaminShot = true)
        item("Матча с коллагеном", MenuCategory.MATCHA, photoUrl = "/menu/матча с коллагеном.png",
            volumes = listOf(350 to 350), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)

        // --- NON_COFFEE ---
        item("Какао", MenuCategory.NON_COFFEE, photoUrl = "/menu/какао.png",
            volumes = listOf(250 to 280, 350 to 310, 450 to 340), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Айс какао", MenuCategory.NON_COFFEE, photoUrl = "/menu/айс какао.png",
            volumes = listOf(350 to 310, 450 to 340), syrup = true, altMilk = true, vitaminShot = true)
        item("Горячий шоколад", MenuCategory.NON_COFFEE, photoUrl = "/menu/горячий шоколад.png",
            volumes = listOf(250 to 350, 350 to 370, 450 to 390), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Чай облепиха", MenuCategory.NON_COFFEE, photoUrl = "/menu/чай облепиха.png",
            volumes = listOf(250 to 220, 350 to 250, 450 to 270), syrup = true, vitaminShot = true)
        item("Чай малина", MenuCategory.NON_COFFEE, photoUrl = "/menu/чай малина.png",
            volumes = listOf(250 to 220, 350 to 250, 450 to 270), syrup = true, vitaminShot = true)
        item("Чай классик", MenuCategory.NON_COFFEE, photoUrl = "/menu/чай классик.png",
            volumes = listOf(250 to 90), syrup = true, vitaminShot = true)
        item("Лимонад классик", MenuCategory.NON_COFFEE, photoUrl = "/menu/лимонад классик.png",
            volumes = listOf(350 to 260, 450 to 300), syrup = true, vitaminShot = true)
        item("Апероль б/а", MenuCategory.NON_COFFEE, photoUrl = "/menu/апероль ба.png",
            volumes = listOf(350 to 300, 450 to 350), syrup = true, vitaminShot = true)
        item("Ананасовый сок", MenuCategory.NON_COFFEE, photoUrl = "/menu/ананасовый сок.png",
            volumes = listOf(450 to 120), syrup = true, vitaminShot = true)

        // --- SIGNATURE ---
        item("Зилант", MenuCategory.SIGNATURE, description = "Сливочный раф с жжёной карамелью и шоколадной крошкой",
            photoUrl = "/menu/зилант.png",
            volumes = listOf(250 to 330, 350 to 360, 450 to 390), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Мрия", MenuCategory.SIGNATURE, description = "Розовый раф с белым шоколадом",
            photoUrl = "/menu/мрия (сейчас ялта).png",
            volumes = listOf(250 to 330, 350 to 360, 450 to 390), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
        item("Есентай", MenuCategory.SIGNATURE, description = "Ходжича — японский чай с нотками ириски и коллагеном",
            photoUrl = "/menu/есентай.png",
            volumes = listOf(250 to 330, 350 to 360, 450 to 390), syrup = true, marshmallow = true, altMilk = true, vitaminShot = true)
    }

    private fun seedAdminUser() {
        if (userRepository.existsByPhone("+70000000000")) return
        userRepository.save(
            User(
                name = "Администратор",
                phone = "+70000000000",
                role = Role.ADMIN,
                passwordHash = passwordEncoder.encode("admin123"),
            ),
        )
        log.info("DataInitializer: admin user created — phone: +70000000000 (see dev seed config for password)")
    }

    private data class ModifierMap(
        val syrups: List<Modifier>,
        val marshmallow: List<Modifier>,
        val altMilk: List<Modifier>,
        val vitaminShots: List<Modifier>,
    )
}
