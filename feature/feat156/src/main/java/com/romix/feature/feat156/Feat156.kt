package com.romix.feature.feat156

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat156Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat156UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat156FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat156UserSummary
)

data class Feat156UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat156NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat156Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat156Config = Feat156Config()
) {

    fun loadSnapshot(userId: Long): Feat156NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat156NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat156UserSummary {
        return Feat156UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat156FeedItem> {
        val result = java.util.ArrayList<Feat156FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat156FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat156UiMapper {

    fun mapToUi(model: List<Feat156FeedItem>): Feat156UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat156UiModel(
            header = UiText("Feat156 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat156UiModel =
        Feat156UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat156UiModel =
        Feat156UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat156UiModel =
        Feat156UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat156Service(
    private val repository: Feat156Repository,
    private val uiMapper: Feat156UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat156UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat156UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat156UserItem1(val user: CoreUser, val label: String)
data class Feat156UserItem2(val user: CoreUser, val label: String)
data class Feat156UserItem3(val user: CoreUser, val label: String)
data class Feat156UserItem4(val user: CoreUser, val label: String)
data class Feat156UserItem5(val user: CoreUser, val label: String)
data class Feat156UserItem6(val user: CoreUser, val label: String)
data class Feat156UserItem7(val user: CoreUser, val label: String)
data class Feat156UserItem8(val user: CoreUser, val label: String)
data class Feat156UserItem9(val user: CoreUser, val label: String)
data class Feat156UserItem10(val user: CoreUser, val label: String)

data class Feat156StateBlock1(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock2(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock3(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock4(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock5(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock6(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock7(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock8(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock9(val state: Feat156UiModel, val checksum: Int)
data class Feat156StateBlock10(val state: Feat156UiModel, val checksum: Int)

fun buildFeat156UserItem(user: CoreUser, index: Int): Feat156UserItem1 {
    return Feat156UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat156StateBlock(model: Feat156UiModel): Feat156StateBlock1 {
    return Feat156StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat156UserSummary> {
    val list = java.util.ArrayList<Feat156UserSummary>(users.size)
    for (user in users) {
        list += Feat156UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat156UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat156UiModel {
    val summaries = (0 until count).map {
        Feat156UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat156UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat156UiModel> {
    val models = java.util.ArrayList<Feat156UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat156AnalyticsEvent1(val name: String, val value: String)
data class Feat156AnalyticsEvent2(val name: String, val value: String)
data class Feat156AnalyticsEvent3(val name: String, val value: String)
data class Feat156AnalyticsEvent4(val name: String, val value: String)
data class Feat156AnalyticsEvent5(val name: String, val value: String)
data class Feat156AnalyticsEvent6(val name: String, val value: String)
data class Feat156AnalyticsEvent7(val name: String, val value: String)
data class Feat156AnalyticsEvent8(val name: String, val value: String)
data class Feat156AnalyticsEvent9(val name: String, val value: String)
data class Feat156AnalyticsEvent10(val name: String, val value: String)

fun logFeat156Event1(event: Feat156AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat156Event2(event: Feat156AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat156Event3(event: Feat156AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat156Event4(event: Feat156AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat156Event5(event: Feat156AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat156Event6(event: Feat156AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat156Event7(event: Feat156AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat156Event8(event: Feat156AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat156Event9(event: Feat156AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat156Event10(event: Feat156AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat156Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat156Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat156(u: CoreUser): Feat156Projection1 =
    Feat156Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat156Projection1> {
    val list = java.util.ArrayList<Feat156Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat156(u)
    }
    return list
}
