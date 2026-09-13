package com.mindbloom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindbloom.app.ui.theme.MB
import com.mindbloom.app.ui.theme.Teal

/* ============================ Palette used by the artwork ================= */

private val Skin = Color(0xFFF6D4AC)
private val SkinShade = Color(0xFFEFC191)
private val HairDark = Color(0xFF2E2450)
private val BodyPurple = Color(0xFF9F7CF2)
private val BodyPurpleDeep = Color(0xFF6B4EE6)
private val MintBg = Color(0xFFD3F5EA)
private val LavenderBg = Color(0xFFE9E4FC)
private val LeafTeal = Color(0xFF2DD4BF)
private val LeafTealDeep = Color(0xFF14B8A6)
private val RobotNavy = Color(0xFF152142)
private val RobotBody = Color(0xFFDCEAFE)
private val RobotOutline = Color(0xFF6FA8F5)

/* ================================ App logo =============================== */

/**
 * The splash mark: an open arc, a head in profile holding a brain and heart,
 * a lotus opening to the right and two leaves at the base.
 */
@Composable
fun MindBloomLogo(modifier: Modifier = Modifier, size: Int = 180) {
    Canvas(modifier = modifier.size(size.dp)) {
        val s = this.size.minDimension
        val c = Offset(s / 2f, s / 2f)

        // Open circle arc, teal fading into blue.
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADEC7), Color(0xFF4F8DF5)),
                start = Offset(0f, s),
                end = Offset(s, 0f)
            ),
            startAngle = 130f,
            sweepAngle = 285f,
            useCenter = false,
            topLeft = Offset(s * 0.06f, s * 0.06f),
            size = Size(s * 0.88f, s * 0.88f),
            style = Stroke(width = s * 0.022f, cap = StrokeCap.Round)
        )

        // Dots trailing off the lower-left of the arc.
        listOf(
            Triple(0.17f, 0.60f, 0.020f) to Color(0xFF4ADEC7),
            Triple(0.13f, 0.68f, 0.018f) to Color(0xFF56C7D6),
            Triple(0.11f, 0.76f, 0.016f) to Color(0xFF7C6BE8),
            Triple(0.13f, 0.84f, 0.014f) to Color(0xFF6B4EE6)
        ).forEach { (pos, color) ->
            drawCircle(color, radius = s * pos.third, center = Offset(s * pos.first, s * pos.second))
        }

        // Lotus petals, drawn back to front from the outside in.
        val petalBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFB79BF5), Color(0xFF8B6FF0)),
            start = Offset(s * 0.55f, s * 0.2f),
            end = Offset(s * 0.9f, s * 0.6f)
        )
        val petalBase = Offset(s * 0.62f, s * 0.58f)
        listOf(-58f, -30f, -2f, 26f).forEach { angle ->
            rotate(angle, pivot = petalBase) {
                val petal = Path().apply {
                    moveTo(petalBase.x, petalBase.y)
                    cubicTo(
                        petalBase.x + s * 0.02f, petalBase.y - s * 0.24f,
                        petalBase.x + s * 0.16f, petalBase.y - s * 0.28f,
                        petalBase.x + s * 0.22f, petalBase.y - s * 0.16f
                    )
                    cubicTo(
                        petalBase.x + s * 0.25f, petalBase.y - s * 0.06f,
                        petalBase.x + s * 0.14f, petalBase.y + s * 0.01f,
                        petalBase.x, petalBase.y
                    )
                    close()
                }
                drawPath(petal, petalBrush)
            }
        }

        // Head in profile, facing left.
        val headBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF63D3E8), Color(0xFF4F7BF0)),
            startY = s * 0.25f,
            endY = s * 0.72f
        )
        val head = Path().apply {
            moveTo(s * 0.46f, s * 0.26f)
            cubicTo(s * 0.30f, s * 0.26f, s * 0.22f, s * 0.38f, s * 0.24f, s * 0.48f)
            cubicTo(s * 0.25f, s * 0.53f, s * 0.21f, s * 0.55f, s * 0.22f, s * 0.58f)
            cubicTo(s * 0.23f, s * 0.61f, s * 0.27f, s * 0.60f, s * 0.28f, s * 0.63f)
            cubicTo(s * 0.29f, s * 0.68f, s * 0.31f, s * 0.72f, s * 0.37f, s * 0.72f)
            lineTo(s * 0.50f, s * 0.72f)
            lineTo(s * 0.50f, s * 0.26f)
            close()
        }
        drawPath(head, headBrush)

        // Brain: a soft circle of loops sitting inside the skull.
        val brainCentre = Offset(s * 0.45f, s * 0.42f)
        drawCircle(
            color = Color.White.copy(alpha = 0.92f),
            radius = s * 0.135f,
            center = brainCentre,
            style = Stroke(width = s * 0.016f)
        )
        listOf(-0.06f, 0f, 0.06f).forEach { dy ->
            val fold = Path().apply {
                moveTo(brainCentre.x - s * 0.10f, brainCentre.y + s * dy)
                cubicTo(
                    brainCentre.x - s * 0.04f, brainCentre.y + s * (dy - 0.035f),
                    brainCentre.x + s * 0.04f, brainCentre.y + s * (dy + 0.035f),
                    brainCentre.x + s * 0.10f, brainCentre.y + s * dy
                )
            }
            drawPath(
                fold,
                color = Color.White.copy(alpha = 0.85f),
                style = Stroke(width = s * 0.012f, cap = StrokeCap.Round)
            )
        }

        // Heart at the centre of the brain.
        val heart = Path().apply {
            val hx = brainCentre.x
            val hy = brainCentre.y - s * 0.005f
            val r = s * 0.030f
            moveTo(hx, hy + r * 1.2f)
            cubicTo(hx - r * 1.8f, hy - r * 0.2f, hx - r * 0.7f, hy - r * 1.5f, hx, hy - r * 0.4f)
            cubicTo(hx + r * 0.7f, hy - r * 1.5f, hx + r * 1.8f, hy - r * 0.2f, hx, hy + r * 1.2f)
            close()
        }
        drawPath(heart, Color.White)

        // Stem and the two leaves at the base.
        drawLine(
            color = Color(0xFF4F8DF5),
            start = Offset(s * 0.55f, s * 0.60f),
            end = Offset(s * 0.55f, s * 0.86f),
            strokeWidth = s * 0.014f,
            cap = StrokeCap.Round
        )
        val leafBrush = Brush.linearGradient(
            listOf(Color(0xFF5FE3CE), Color(0xFF2DD4BF))
        )
        val leftLeaf = Path().apply {
            moveTo(s * 0.55f, s * 0.82f)
            cubicTo(s * 0.44f, s * 0.84f, s * 0.35f, s * 0.79f, s * 0.31f, s * 0.71f)
            cubicTo(s * 0.42f, s * 0.68f, s * 0.52f, s * 0.72f, s * 0.55f, s * 0.82f)
            close()
        }
        val rightLeaf = Path().apply {
            moveTo(s * 0.56f, s * 0.82f)
            cubicTo(s * 0.67f, s * 0.84f, s * 0.76f, s * 0.79f, s * 0.80f, s * 0.71f)
            cubicTo(s * 0.69f, s * 0.68f, s * 0.59f, s * 0.72f, s * 0.56f, s * 0.82f)
            close()
        }
        drawPath(leftLeaf, leafBrush)
        drawPath(rightLeaf, leafBrush)

        // Sparkles.
        drawSparkle(Offset(s * 0.33f, s * 0.34f), s * 0.028f, Color(0xFF8B6FF0))
        drawSparkle(Offset(s * 0.68f, s * 0.62f), s * 0.024f, Color(0xFF4F8DF5))
        drawSparkle(Offset(c.x + s * 0.02f, s * 0.20f), s * 0.020f, Color(0xFF4ADEC7))
    }
}

private fun DrawScope.drawSparkle(centre: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(centre.x, centre.y - radius)
        quadraticTo(centre.x, centre.y, centre.x + radius, centre.y)
        quadraticTo(centre.x, centre.y, centre.x, centre.y + radius)
        quadraticTo(centre.x, centre.y, centre.x - radius, centre.y)
        quadraticTo(centre.x, centre.y, centre.x, centre.y - radius)
        close()
    }
    drawPath(path, color)
}

/* ========================= Onboarding illustration 1 ===================== */

/** Seated figure meditating between two plants. */
@Composable
fun MeditationIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Mint backdrop.
        drawOval(
            color = MintBg,
            topLeft = Offset(w * 0.18f, h * 0.06f),
            size = Size(w * 0.64f, h * 0.78f)
        )

        // Floating accent dots.
        drawCircle(LeafTeal, w * 0.017f, Offset(w * 0.30f, h * 0.18f))
        drawCircle(BodyPurple, w * 0.022f, Offset(w * 0.70f, h * 0.12f))
        drawCircle(LeafTeal, w * 0.013f, Offset(w * 0.735f, h * 0.30f))

        // Plants either side.
        drawPottedPlant(Offset(w * 0.20f, h * 0.62f), w * 0.115f, h * 0.24f, BodyPurple)
        drawPottedPlant(Offset(w * 0.665f, h * 0.62f), w * 0.115f, h * 0.24f, BodyPurpleDeep)

        // Cushion / crossed legs.
        drawOval(
            color = BodyPurpleDeep,
            topLeft = Offset(w * 0.335f, h * 0.70f),
            size = Size(w * 0.33f, h * 0.16f)
        )

        // Torso.
        val torso = Path().apply {
            moveTo(w * 0.40f, h * 0.76f)
            cubicTo(w * 0.39f, h * 0.50f, w * 0.44f, h * 0.42f, w * 0.50f, h * 0.42f)
            cubicTo(w * 0.56f, h * 0.42f, w * 0.61f, h * 0.50f, w * 0.60f, h * 0.76f)
            close()
        }
        drawPath(torso, BodyPurple)

        // Arms resting on the knees.
        drawArmCurve(Offset(w * 0.415f, h * 0.50f), Offset(w * 0.345f, h * 0.74f), w * 0.045f, true)
        drawArmCurve(Offset(w * 0.585f, h * 0.50f), Offset(w * 0.655f, h * 0.74f), w * 0.045f, false)

        // Head, hair and bun.
        val headCentre = Offset(w * 0.50f, h * 0.335f)
        val headR = w * 0.082f
        drawCircle(Skin, headR, headCentre)
        val hair = Path().apply {
            moveTo(headCentre.x - headR, headCentre.y - headR * 0.05f)
            cubicTo(
                headCentre.x - headR, headCentre.y - headR * 1.4f,
                headCentre.x + headR, headCentre.y - headR * 1.4f,
                headCentre.x + headR, headCentre.y - headR * 0.05f
            )
            cubicTo(
                headCentre.x + headR * 0.7f, headCentre.y - headR * 0.55f,
                headCentre.x - headR * 0.7f, headCentre.y - headR * 0.55f,
                headCentre.x - headR, headCentre.y - headR * 0.05f
            )
            close()
        }
        drawPath(hair, HairDark)
        drawCircle(HairDark, headR * 0.42f, Offset(headCentre.x, headCentre.y - headR * 1.25f))

        // Closed eyes and a small smile.
        drawClosedEye(Offset(headCentre.x - headR * 0.42f, headCentre.y + headR * 0.05f), headR * 0.22f)
        drawClosedEye(Offset(headCentre.x + headR * 0.42f, headCentre.y + headR * 0.05f), headR * 0.22f)
        drawArc(
            color = HairDark,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(headCentre.x - headR * 0.26f, headCentre.y + headR * 0.20f),
            size = Size(headR * 0.52f, headR * 0.36f),
            style = Stroke(width = headR * 0.10f, cap = StrokeCap.Round)
        )
    }
}

/* ========================= Onboarding illustration 2 ===================== */

/** Figure watering a growing plant. */
@Composable
fun WateringIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawOval(
            color = LavenderBg,
            topLeft = Offset(w * 0.20f, h * 0.10f),
            size = Size(w * 0.62f, h * 0.68f)
        )
        drawCircle(LeafTeal, w * 0.016f, Offset(w * 0.27f, h * 0.20f))
        drawCircle(LeafTeal, w * 0.013f, Offset(w * 0.245f, h * 0.42f))
        drawCircle(BodyPurple, w * 0.020f, Offset(w * 0.775f, h * 0.20f))

        // Ground shadow.
        drawOval(
            color = Color(0x11463A7A),
            topLeft = Offset(w * 0.24f, h * 0.72f),
            size = Size(w * 0.52f, h * 0.09f)
        )

        // Body.
        drawRoundRect(
            color = BodyPurple,
            topLeft = Offset(w * 0.345f, h * 0.36f),
            size = Size(w * 0.145f, h * 0.40f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
        )

        // Head with a side ponytail.
        val headCentre = Offset(w * 0.415f, h * 0.245f)
        val headR = w * 0.072f
        drawCircle(Skin, headR, headCentre)
        val fringe = Path().apply {
            moveTo(headCentre.x - headR, headCentre.y - headR * 0.1f)
            cubicTo(
                headCentre.x - headR, headCentre.y - headR * 1.35f,
                headCentre.x + headR, headCentre.y - headR * 1.35f,
                headCentre.x + headR, headCentre.y - headR * 0.1f
            )
            cubicTo(
                headCentre.x + headR * 0.6f, headCentre.y - headR * 0.6f,
                headCentre.x - headR * 0.6f, headCentre.y - headR * 0.6f,
                headCentre.x - headR, headCentre.y - headR * 0.1f
            )
            close()
        }
        drawPath(fringe, HairDark)
        val ponytail = Path().apply {
            moveTo(headCentre.x + headR * 0.75f, headCentre.y - headR * 0.5f)
            cubicTo(
                headCentre.x + headR * 1.7f, headCentre.y + headR * 0.1f,
                headCentre.x + headR * 1.5f, headCentre.y + headR * 1.5f,
                headCentre.x + headR * 0.6f, headCentre.y + headR * 1.6f
            )
            cubicTo(
                headCentre.x + headR * 1.0f, headCentre.y + headR * 0.9f,
                headCentre.x + headR * 1.0f, headCentre.y + headR * 0.2f,
                headCentre.x + headR * 0.75f, headCentre.y - headR * 0.5f
            )
            close()
        }
        drawPath(ponytail, HairDark)
        drawClosedEye(Offset(headCentre.x - headR * 0.30f, headCentre.y + headR * 0.08f), headR * 0.20f)
        drawClosedEye(Offset(headCentre.x + headR * 0.35f, headCentre.y + headR * 0.08f), headR * 0.20f)

        // Arm reaching towards the watering can.
        drawArmCurve(Offset(w * 0.465f, h * 0.42f), Offset(w * 0.545f, h * 0.40f), w * 0.036f, false)

        // Watering can.
        drawRoundRect(
            color = BodyPurpleDeep,
            topLeft = Offset(w * 0.545f, h * 0.315f),
            size = Size(w * 0.115f, h * 0.115f),
            cornerRadius = CornerRadius(w * 0.028f, w * 0.028f)
        )
        val spout = Path().apply {
            moveTo(w * 0.655f, h * 0.345f)
            lineTo(w * 0.725f, h * 0.315f)
            lineTo(w * 0.725f, h * 0.375f)
            lineTo(w * 0.655f, h * 0.395f)
            close()
        }
        drawPath(spout, BodyPurpleDeep)
        drawArc(
            color = BodyPurpleDeep,
            startAngle = 200f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(w * 0.565f, h * 0.275f),
            size = Size(w * 0.075f, h * 0.075f),
            style = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
        )

        // Water falling onto the plant.
        listOf(0f, 0.035f, 0.07f).forEachIndexed { index, dy ->
            drawCircle(
                color = LeafTeal.copy(alpha = 0.9f - index * 0.2f),
                radius = w * 0.010f,
                center = Offset(w * (0.735f + index * 0.006f), h * (0.395f + dy))
            )
        }

        // Pot and plant.
        val pot = Path().apply {
            moveTo(w * 0.615f, h * 0.545f)
            lineTo(w * 0.775f, h * 0.545f)
            lineTo(w * 0.755f, h * 0.735f)
            lineTo(w * 0.635f, h * 0.735f)
            close()
        }
        drawPath(pot, LeafTeal)
        drawLine(
            color = HairDark,
            start = Offset(w * 0.695f, h * 0.545f),
            end = Offset(w * 0.695f, h * 0.375f),
            strokeWidth = w * 0.010f,
            cap = StrokeCap.Round
        )
        val sprout = Path().apply {
            moveTo(w * 0.695f, h * 0.405f)
            cubicTo(w * 0.735f, h * 0.355f, w * 0.775f, h * 0.365f, w * 0.785f, h * 0.395f)
            cubicTo(w * 0.750f, h * 0.425f, w * 0.715f, h * 0.425f, w * 0.695f, h * 0.405f)
            close()
        }
        drawPath(sprout, LeafTealDeep)
    }
}

/* ========================= Onboarding illustration 3 ===================== */

/** Bloom Bot surrounded by floating summary cards. */
@Composable
fun AiInsightsIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = LavenderBg,
                radius = h * 0.42f,
                center = Offset(w * 0.52f, h * 0.46f)
            )
            translate(left = w * 0.30f, top = h * 0.06f) {
                drawBloomBot(Size(w * 0.42f, h * 0.80f))
            }
        }

        // Floating data cards.
        FloatingCard(modifier = Modifier.offset(x = (-118).dp, y = (-38).dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                MiniBar(height = 18, color = BodyPurple)
                Spacer(Modifier.width(4.dp))
                MiniBar(height = 26, color = BodyPurpleDeep)
                Spacer(Modifier.width(4.dp))
                MiniBar(height = 12, color = Teal)
            }
            Spacer(Modifier.height(4.dp))
            Text("Mood", fontSize = 11.sp, color = MB.colors.textSecondary)
        }

        FloatingCard(modifier = Modifier.offset(x = 112.dp, y = (-6).dp)) {
            Canvas(modifier = Modifier.size(54.dp, 20.dp)) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.85f)
                    lineTo(size.width * 0.28f, size.height * 0.45f)
                    lineTo(size.width * 0.52f, size.height * 0.62f)
                    lineTo(size.width, size.height * 0.08f)
                }
                drawPath(
                    path,
                    color = Color(0xFF22B85C),
                    style = Stroke(width = 2.5f.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text("+18% calm", fontSize = 11.sp, color = Color(0xFF22B85C))
        }

        FloatingCard(modifier = Modifier.offset(x = (-108).dp, y = 52.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(BodyPurpleDeep))
                Spacer(Modifier.width(6.dp))
                Text("Score 72", fontSize = 11.sp, color = MB.colors.textPrimary)
            }
        }
    }
}

@Composable
private fun MiniBar(height: Int, color: Color) {
    Box(
        Modifier
            .width(7.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
    )
}

@Composable
private fun FloatingCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(12.dp))
            .background(MB.colors.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) { content() }
    }
}

/* ================================ Bloom Bot ============================== */

/** The mascot on its own — used on the dashboard banner and insights header. */
@Composable
fun BloomBotBadge(modifier: Modifier = Modifier, size: Int = 56) {
    Canvas(modifier = modifier.size(size.dp)) {
        drawBloomBot(this.size)
    }
}

private fun DrawScope.drawBloomBot(area: Size) {
    val w = area.width
    val h = area.height
    val cx = w / 2f

    // Antenna.
    drawLine(
        color = Teal,
        start = Offset(cx, h * 0.10f),
        end = Offset(cx, h * 0.20f),
        strokeWidth = w * 0.045f,
        cap = StrokeCap.Round
    )
    drawCircle(Teal, w * 0.075f, Offset(cx, h * 0.08f))

    // Lower body.
    drawRoundRect(
        color = RobotBody,
        topLeft = Offset(w * 0.20f, h * 0.60f),
        size = Size(w * 0.60f, h * 0.32f),
        cornerRadius = CornerRadius(w * 0.20f, w * 0.20f)
    )
    drawRoundRect(
        color = RobotOutline,
        topLeft = Offset(w * 0.20f, h * 0.60f),
        size = Size(w * 0.60f, h * 0.32f),
        cornerRadius = CornerRadius(w * 0.20f, w * 0.20f),
        style = Stroke(width = w * 0.030f)
    )
    drawCircle(Teal, w * 0.085f, Offset(cx, h * 0.755f))

    // Side arms.
    drawRoundRect(
        color = RobotOutline,
        topLeft = Offset(w * 0.05f, h * 0.36f),
        size = Size(w * 0.09f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
    )
    drawRoundRect(
        color = RobotOutline,
        topLeft = Offset(w * 0.86f, h * 0.36f),
        size = Size(w * 0.09f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
    )

    // Head shell.
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(w * 0.13f, h * 0.19f),
        size = Size(w * 0.74f, h * 0.44f),
        cornerRadius = CornerRadius(w * 0.20f, w * 0.20f)
    )
    drawRoundRect(
        color = RobotOutline,
        topLeft = Offset(w * 0.13f, h * 0.19f),
        size = Size(w * 0.74f, h * 0.44f),
        cornerRadius = CornerRadius(w * 0.20f, w * 0.20f),
        style = Stroke(width = w * 0.035f)
    )

    // Visor.
    drawRoundRect(
        color = RobotNavy,
        topLeft = Offset(w * 0.22f, h * 0.26f),
        size = Size(w * 0.56f, h * 0.30f),
        cornerRadius = CornerRadius(w * 0.15f, w * 0.15f)
    )
    drawCircle(Teal, w * 0.070f, Offset(cx - w * 0.135f, h * 0.41f))
    drawCircle(Teal, w * 0.070f, Offset(cx + w * 0.135f, h * 0.41f))
}

/* =============================== Confetti ================================ */

data class ConfettiPiece(
    val xFraction: Float,
    val yFraction: Float,
    val rotation: Float,
    val color: Color,
    val round: Boolean
)

/** Deterministic confetti layout matching the celebration screen. */
val successConfetti: List<ConfettiPiece> = listOf(
    ConfettiPiece(0.10f, 0.14f, 25f, Color(0xFF9F7CF2), false),
    ConfettiPiece(0.27f, 0.08f, -20f, Color(0xFF22B85C), false),
    ConfettiPiece(0.55f, 0.06f, 10f, Color(0xFFEF4444), true),
    ConfettiPiece(0.74f, 0.12f, 40f, Color(0xFF2DD4BF), true),
    ConfettiPiece(0.85f, 0.09f, -35f, Color(0xFFB79BF5), false),
    ConfettiPiece(0.17f, 0.22f, 55f, Color(0xFF9F7CF2), false),
    ConfettiPiece(0.38f, 0.17f, -10f, Color(0xFF2DD4BF), true),
    ConfettiPiece(0.62f, 0.20f, 70f, Color(0xFFF59E0B), false),
    ConfettiPiece(0.90f, 0.25f, 15f, Color(0xFF6B4EE6), false),
    ConfettiPiece(0.06f, 0.31f, -45f, Color(0xFFF59E0B), false),
    ConfettiPiece(0.33f, 0.28f, 30f, Color(0xFFEF4444), false),
    ConfettiPiece(0.51f, 0.30f, -25f, Color(0xFF2DD4BF), false),
    ConfettiPiece(0.79f, 0.33f, 60f, Color(0xFF22B85C), false),
    ConfettiPiece(0.12f, 0.40f, 20f, Color(0xFFF59E0B), false),
    ConfettiPiece(0.24f, 0.46f, -55f, Color(0xFF2DD4BF), false),
    ConfettiPiece(0.44f, 0.43f, 35f, Color(0xFFEF4444), false),
    ConfettiPiece(0.68f, 0.45f, -15f, Color(0xFF9F7CF2), true),
    ConfettiPiece(0.88f, 0.41f, 50f, Color(0xFF22B85C), false),
    ConfettiPiece(0.05f, 0.52f, 10f, Color(0xFFF59E0B), true),
    ConfettiPiece(0.30f, 0.56f, -30f, Color(0xFF6B4EE6), true),
    ConfettiPiece(0.58f, 0.53f, 45f, Color(0xFF9F7CF2), false),
    ConfettiPiece(0.83f, 0.58f, -40f, Color(0xFF2DD4BF), false),
    ConfettiPiece(0.16f, 0.63f, 65f, Color(0xFF2DD4BF), false),
    ConfettiPiece(0.47f, 0.66f, -20f, Color(0xFF22B85C), true),
    ConfettiPiece(0.72f, 0.62f, 25f, Color(0xFFB79BF5), false)
)

/* ============================ Drawing helpers ============================ */

private fun DrawScope.drawPottedPlant(
    topLeft: Offset,
    width: Float,
    height: Float,
    potColor: Color
) {
    drawRoundRect(
        color = potColor,
        topLeft = topLeft,
        size = Size(width, height),
        cornerRadius = CornerRadius(width * 0.10f, width * 0.10f)
    )
    val stemX = topLeft.x + width / 2f
    val leafLeft = Path().apply {
        moveTo(stemX, topLeft.y)
        cubicTo(
            stemX - width * 0.85f, topLeft.y - height * 0.28f,
            stemX - width * 0.70f, topLeft.y - height * 0.86f,
            stemX - width * 0.10f, topLeft.y - height * 0.60f
        )
        close()
    }
    val leafRight = Path().apply {
        moveTo(stemX, topLeft.y)
        cubicTo(
            stemX + width * 0.85f, topLeft.y - height * 0.28f,
            stemX + width * 0.70f, topLeft.y - height * 0.86f,
            stemX + width * 0.10f, topLeft.y - height * 0.60f
        )
        close()
    }
    drawPath(leafLeft, LeafTeal)
    drawPath(leafRight, LeafTealDeep)
}

private fun DrawScope.drawArmCurve(
    from: Offset,
    to: Offset,
    thickness: Float,
    bendLeft: Boolean
) {
    val bend = if (bendLeft) -thickness * 2.2f else thickness * 2.2f
    val path = Path().apply {
        moveTo(from.x, from.y)
        cubicTo(
            from.x + bend, from.y + (to.y - from.y) * 0.45f,
            to.x + bend * 0.3f, to.y - (to.y - from.y) * 0.25f,
            to.x, to.y
        )
    }
    drawPath(path, Skin, style = Stroke(width = thickness * 2f, cap = StrokeCap.Round))
    drawCircle(SkinShade, thickness * 0.85f, to)
}

private fun DrawScope.drawClosedEye(centre: Offset, radius: Float) {
    drawArc(
        color = HairDark,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(centre.x - radius, centre.y - radius),
        size = Size(radius * 2f, radius * 2f),
        style = Stroke(width = radius * 0.45f, cap = StrokeCap.Round)
    )
}
