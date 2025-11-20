package com.romix.feature.feat108

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat108Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat108UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat108FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat108UserSummary
)

data class Feat108UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat108NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat108Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat108Config = Feat108Config()
) {

    fun loadSnapshot(userId: Long): Feat108NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat108NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat108UserSummary {
        return Feat108UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat108FeedItem> {
        val result = java.util.ArrayList<Feat108FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat108FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat108UiMapper {

    fun mapToUi(model: List<Feat108FeedItem>): Feat108UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat108UiModel(
            header = UiText("Feat108 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat108UiModel =
        Feat108UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat108UiModel =
        Feat108UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat108UiModel =
        Feat108UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat108Service(
    private val repository: Feat108Repository,
    private val uiMapper: Feat108UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat108UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat108UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat108UserItem1(val user: CoreUser, val label: String)
data class Feat108UserItem2(val user: CoreUser, val label: String)
data class Feat108UserItem3(val user: CoreUser, val label: String)
data class Feat108UserItem4(val user: CoreUser, val label: String)
data class Feat108UserItem5(val user: CoreUser, val label: String)
data class Feat108UserItem6(val user: CoreUser, val label: String)
data class Feat108UserItem7(val user: CoreUser, val label: String)
data class Feat108UserItem8(val user: CoreUser, val label: String)
data class Feat108UserItem9(val user: CoreUser, val label: String)
data class Feat108UserItem10(val user: CoreUser, val label: String)

data class Feat108StateBlock1(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock2(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock3(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock4(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock5(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock6(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock7(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock8(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock9(val state: Feat108UiModel, val checksum: Int)
data class Feat108StateBlock10(val state: Feat108UiModel, val checksum: Int)

fun buildFeat108UserItem(user: CoreUser, index: Int): Feat108UserItem1 {
    return Feat108UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat108StateBlock(model: Feat108UiModel): Feat108StateBlock1 {
    return Feat108StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat108UserSummary> {
    val list = java.util.ArrayList<Feat108UserSummary>(users.size)
    for (user in users) {
        list += Feat108UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat108UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat108UiModel {
    val summaries = (0 until count).map {
        Feat108UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat108UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat108UiModel> {
    val models = java.util.ArrayList<Feat108UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat108AnalyticsEvent1(val name: String, val value: String)
data class Feat108AnalyticsEvent2(val name: String, val value: String)
data class Feat108AnalyticsEvent3(val name: String, val value: String)
data class Feat108AnalyticsEvent4(val name: String, val value: String)
data class Feat108AnalyticsEvent5(val name: String, val value: String)
data class Feat108AnalyticsEvent6(val name: String, val value: String)
data class Feat108AnalyticsEvent7(val name: String, val value: String)
data class Feat108AnalyticsEvent8(val name: String, val value: String)
data class Feat108AnalyticsEvent9(val name: String, val value: String)
data class Feat108AnalyticsEvent10(val name: String, val value: String)

fun logFeat108Event1(event: Feat108AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat108Event2(event: Feat108AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat108Event3(event: Feat108AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat108Event4(event: Feat108AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat108Event5(event: Feat108AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat108Event6(event: Feat108AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat108Event7(event: Feat108AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat108Event8(event: Feat108AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat108Event9(event: Feat108AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat108Event10(event: Feat108AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat108Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat108Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat108(u: CoreUser): Feat108Projection1 =
    Feat108Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat108Projection1> {
    val list = java.util.ArrayList<Feat108Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat108(u)
    }
    return list
}
