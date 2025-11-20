package com.romix.feature.feat575

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat575Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat575UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat575FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat575UserSummary
)

data class Feat575UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat575NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat575Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat575Config = Feat575Config()
) {

    fun loadSnapshot(userId: Long): Feat575NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat575NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat575UserSummary {
        return Feat575UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat575FeedItem> {
        val result = java.util.ArrayList<Feat575FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat575FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat575UiMapper {

    fun mapToUi(model: List<Feat575FeedItem>): Feat575UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat575UiModel(
            header = UiText("Feat575 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat575UiModel =
        Feat575UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat575UiModel =
        Feat575UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat575UiModel =
        Feat575UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat575Service(
    private val repository: Feat575Repository,
    private val uiMapper: Feat575UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat575UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat575UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat575UserItem1(val user: CoreUser, val label: String)
data class Feat575UserItem2(val user: CoreUser, val label: String)
data class Feat575UserItem3(val user: CoreUser, val label: String)
data class Feat575UserItem4(val user: CoreUser, val label: String)
data class Feat575UserItem5(val user: CoreUser, val label: String)
data class Feat575UserItem6(val user: CoreUser, val label: String)
data class Feat575UserItem7(val user: CoreUser, val label: String)
data class Feat575UserItem8(val user: CoreUser, val label: String)
data class Feat575UserItem9(val user: CoreUser, val label: String)
data class Feat575UserItem10(val user: CoreUser, val label: String)

data class Feat575StateBlock1(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock2(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock3(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock4(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock5(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock6(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock7(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock8(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock9(val state: Feat575UiModel, val checksum: Int)
data class Feat575StateBlock10(val state: Feat575UiModel, val checksum: Int)

fun buildFeat575UserItem(user: CoreUser, index: Int): Feat575UserItem1 {
    return Feat575UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat575StateBlock(model: Feat575UiModel): Feat575StateBlock1 {
    return Feat575StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat575UserSummary> {
    val list = java.util.ArrayList<Feat575UserSummary>(users.size)
    for (user in users) {
        list += Feat575UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat575UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat575UiModel {
    val summaries = (0 until count).map {
        Feat575UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat575UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat575UiModel> {
    val models = java.util.ArrayList<Feat575UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat575AnalyticsEvent1(val name: String, val value: String)
data class Feat575AnalyticsEvent2(val name: String, val value: String)
data class Feat575AnalyticsEvent3(val name: String, val value: String)
data class Feat575AnalyticsEvent4(val name: String, val value: String)
data class Feat575AnalyticsEvent5(val name: String, val value: String)
data class Feat575AnalyticsEvent6(val name: String, val value: String)
data class Feat575AnalyticsEvent7(val name: String, val value: String)
data class Feat575AnalyticsEvent8(val name: String, val value: String)
data class Feat575AnalyticsEvent9(val name: String, val value: String)
data class Feat575AnalyticsEvent10(val name: String, val value: String)

fun logFeat575Event1(event: Feat575AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat575Event2(event: Feat575AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat575Event3(event: Feat575AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat575Event4(event: Feat575AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat575Event5(event: Feat575AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat575Event6(event: Feat575AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat575Event7(event: Feat575AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat575Event8(event: Feat575AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat575Event9(event: Feat575AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat575Event10(event: Feat575AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat575Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat575Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat575(u: CoreUser): Feat575Projection1 =
    Feat575Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat575Projection1> {
    val list = java.util.ArrayList<Feat575Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat575(u)
    }
    return list
}
