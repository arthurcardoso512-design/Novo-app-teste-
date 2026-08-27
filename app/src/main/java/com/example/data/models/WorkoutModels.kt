package com.example.data.models

data class ExerciseTemplate(
  val id: String,
  val name: String,
  val icon: String,
  val sets: Int,
  val reps: String,
  val rest: Int, // in seconds
  val rir: String,
  val focus: String,
  val desc: String,
  val alert: String? = null
)

data class WorkoutTemplate(
  val id: String,
  val name: String,
  val subtitle: String,
  val estTime: Int,
  val warmup: String,
  val cardio: String,
  val exercises: List<ExerciseTemplate>
)

data class WorkoutSet(
  val load: String = "",
  val reps: String = "",
  val done: Boolean = false
)

data class SessionExercise(
  val exerciseId: String,
  val sets: List<WorkoutSet>,
  val skipped: Boolean = false,
  val skipReason: String = ""
)

val WORKOUT_ORDER = listOf("A", "B", "C")

val INTERMEDIATE_GOALS = listOf(120.0, 115.0, 110.0, 105.0, 100.0)

val WORKOUT_TEMPLATES = mapOf(
  "A" to WorkoutTemplate(
    id = "A",
    name = "Treino A",
    subtitle = "Full Body — Base de força",
    estTime = 60,
    warmup = "5–7 min de esteira ou bicicleta, seguidos de 1–2 séries leves do primeiro exercício.",
    cardio = "5–10 minutos de caminhada na esteira.",
    exercises = listOf(
      ExerciseTemplate(
        id = "legpress",
        name = "Leg Press",
        icon = "🦵",
        sets = 3,
        reps = "8–12",
        rest = 105,
        rir = "3",
        focus = "Quadríceps, glúteos e posteriores",
        desc = "Controle a descida, mantenha os pés firmes na plataforma e evite perder o contato da lombar com o banco."
      ),
      ExerciseTemplate(
        id = "supino",
        name = "Supino máquina ou halteres",
        icon = "🏋️",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2–3",
        focus = "Peitoral, tríceps e deltoide anterior",
        desc = "Desça controlado até sentir leve alongamento no peito, sem travar os cotovelos no topo."
      ),
      ExerciseTemplate(
        id = "puxada",
        name = "Puxada frontal",
        icon = "🤸",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2–3",
        focus = "Costas e bíceps",
        desc = "Puxe a barra até a altura do queixo contraindo as escápulas, sem balançar o tronco."
      ),
      ExerciseTemplate(
        id = "mesaflexora",
        name = "Mesa flexora",
        icon = "🦵",
        sets = 2,
        reps = "10–15",
        rest = 75,
        rir = "2",
        focus = "Posteriores de coxa",
        desc = "Flexione o joelho controladamente, sem levantar o quadril do banco."
      ),
      ExerciseTemplate(
        id = "desenvolvimento",
        name = "Desenvolvimento de ombros",
        icon = "💪",
        sets = 2,
        reps = "8–12",
        rest = 75,
        rir = "2–3",
        focus = "Deltoides",
        desc = "Empurre o peso para cima sem hiperestender a lombar."
      ),
      ExerciseTemplate(
        id = "tricepspolia",
        name = "Tríceps na polia",
        icon = "💪",
        sets = 2,
        reps = "10–15",
        rest = 60,
        rir = "2",
        focus = "Tríceps",
        desc = "Mantenha os cotovelos fixos ao lado do corpo durante o movimento."
      ),
      ExerciseTemplate(
        id = "core",
        name = "Core (Pallof press)",
        icon = "🧘",
        sets = 2,
        reps = "10–15",
        rest = 60,
        rir = "2",
        focus = "Core e estabilidade",
        desc = "Resista à rotação do tronco mantendo o quadril parado."
      )
    )
  ),
  "B" to WorkoutTemplate(
    id = "B",
    name = "Treino B",
    subtitle = "Pernas + Costas",
    estTime = 60,
    warmup = "5–7 min de bicicleta ou esteira.",
    cardio = "5–10 minutos.",
    exercises = listOf(
      ExerciseTemplate(
        id = "agachsmith",
        name = "Agachamento no Smith",
        icon = "🏋️",
        sets = 3,
        reps = "8–10",
        rest = 105,
        rir = "3",
        focus = "Quadríceps e glúteos",
        desc = "Desça até cerca de 90°, mantendo o peito aberto e os joelhos alinhados aos pés."
      ),
      ExerciseTemplate(
        id = "remadamaquina",
        name = "Remada máquina",
        icon = "🚣",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2–3",
        focus = "Costas",
        desc = "Puxe levando os cotovelos para trás, contraindo as escápulas."
      ),
      ExerciseTemplate(
        id = "supinoinclinado",
        name = "Supino inclinado",
        icon = "🏋️",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2",
        focus = "Peitoral superior",
        desc = "Controle a descida e evite arquear excessivamente a lombar."
      ),
      ExerciseTemplate(
        id = "extensora",
        name = "Cadeira extensora",
        icon = "🦵",
        sets = 2,
        reps = "10–15",
        rest = 75,
        rir = "2",
        focus = "Quadríceps",
        desc = "Estenda o joelho sem travar bruscamente, controlando a volta."
      ),
      ExerciseTemplate(
        id = "flexoraB",
        name = "Flexora",
        icon = "🦵",
        sets = 2,
        reps = "10–15",
        rest = 75,
        rir = "2",
        focus = "Posteriores de coxa",
        desc = "Flexione o joelho controladamente, sem tirar o quadril do banco."
      ),
      ExerciseTemplate(
        id = "elevlateral",
        name = "Elevação lateral",
        icon = "💪",
        sets = 2,
        reps = "12–15",
        rest = 60,
        rir = "2",
        focus = "Deltoide lateral",
        desc = "Eleve os braços até a linha dos ombros, sem usar impulso."
      ),
      ExerciseTemplate(
        id = "roscadireta",
        name = "Rosca direta/máquina",
        icon = "💪",
        sets = 2,
        reps = "10–15",
        rest = 60,
        rir = "2",
        focus = "Bíceps",
        desc = "Mantenha os cotovelos fixos, evitando balançar o tronco."
      )
    )
  ),
  "C" to WorkoutTemplate(
    id = "C",
    name = "Treino C",
    subtitle = "Full Body + Condicionamento",
    estTime = 55,
    warmup = "5 minutos de aquecimento geral.",
    cardio = "8–10 minutos.",
    exercises = listOf(
      ExerciseTemplate(
        id = "legpressC",
        name = "Leg Press",
        icon = "🦵",
        sets = 3,
        reps = "10–12",
        rest = 90,
        rir = "2–3",
        focus = "Quadríceps, glúteos e posteriores",
        desc = "Controle a descida, mantenha os pés firmes na plataforma."
      ),
      ExerciseTemplate(
        id = "puxadaC",
        name = "Puxada frontal",
        icon = "🤸",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2",
        focus = "Costas e bíceps",
        desc = "Puxe a barra até a altura do queixo, contraindo as escápulas."
      ),
      ExerciseTemplate(
        id = "chestpress",
        name = "Chest Press / Supino máquina",
        icon = "🏋️",
        sets = 3,
        reps = "8–12",
        rest = 90,
        rir = "2",
        focus = "Peitoral e tríceps",
        desc = "Empurre de forma controlada, sem travar os cotovelos no topo."
      ),
      ExerciseTemplate(
        id = "terraromeno",
        name = "Levantamento terra romeno",
        icon = "🏋️",
        sets = 2,
        reps = "8–12",
        rest = 90,
        rir = "3",
        focus = "Posteriores e glúteos",
        alert = "Priorize aprender o movimento. Não busque carga máxima. Alternativa: Flexora, caso a execução não esteja confortável.",
        desc = "Mantenha a lombar neutra e desça a barra próxima às pernas."
      ),
      ExerciseTemplate(
        id = "remadabaixa",
        name = "Remada baixa",
        icon = "🚣",
        sets = 2,
        reps = "10–12",
        rest = 75,
        rir = "2",
        focus = "Costas",
        desc = "Puxe levando os cotovelos para trás, mantendo o tronco estável."
      ),
      ExerciseTemplate(
        id = "elevlateralC",
        name = "Elevação lateral",
        icon = "💪",
        sets = 2,
        reps = "12–15",
        rest = 60,
        rir = "2",
        focus = "Deltoide lateral",
        desc = "Eleve os braços até a linha dos ombros, sem impulso."
      ),
      ExerciseTemplate(
        id = "panturrilha",
        name = "Panturrilha",
        icon = "🦵",
        sets = 2,
        reps = "12–15",
        rest = 60,
        rir = "2",
        focus = "Panturrilhas",
        desc = "Suba na ponta dos pés controladamente, pausando no topo."
      )
    )
  )
)
