package com.romix.feature.feat100

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat100Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat100UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat100FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat100UserSummary
)

data class Feat100UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat100NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat100Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat100Config = Feat100Config()
) {

    fun loadSnapshot(userId: Long): Feat100NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat100NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat100UserSummary {
        return Feat100UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat100FeedItem> {
        val result = java.util.ArrayList<Feat100FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat100FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat100UiMapper {

    fun mapToUi(model: List<Feat100FeedItem>): Feat100UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat100UiModel(
            header = UiText("Feat100 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat100UiModel =
        Feat100UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat100UiModel =
        Feat100UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat100UiModel =
        Feat100UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat100Service(
    private val repository: Feat100Repository,
    private val uiMapper: Feat100UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat100UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat100UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat100UserItem1(val user: CoreUser, val label: String)
data class Feat100UserItem2(val user: CoreUser, val label: String)
data class Feat100UserItem3(val user: CoreUser, val label: String)
data class Feat100UserItem4(val user: CoreUser, val label: String)
data class Feat100UserItem5(val user: CoreUser, val label: String)
data class Feat100UserItem6(val user: CoreUser, val label: String)
data class Feat100UserItem7(val user: CoreUser, val label: String)
data class Feat100UserItem8(val user: CoreUser, val label: String)
data class Feat100UserItem9(val user: CoreUser, val label: String)
data class Feat100UserItem10(val user: CoreUser, val label: String)

data class Feat100StateBlock1(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock2(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock3(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock4(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock5(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock6(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock7(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock8(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock9(val state: Feat100UiModel, val checksum: Int)
data class Feat100StateBlock10(val state: Feat100UiModel, val checksum: Int)

fun buildFeat100UserItem(user: CoreUser, index: Int): Feat100UserItem1 {
    return Feat100UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat100StateBlock(model: Feat100UiModel): Feat100StateBlock1 {
    return Feat100StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat100UserSummary> {
    val list = java.util.ArrayList<Feat100UserSummary>(users.size)
    for (user in users) {
        list += Feat100UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat100UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat100UiModel {
    val summaries = (0 until count).map {
        Feat100UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat100UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat100UiModel> {
    val models = java.util.ArrayList<Feat100UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat100AnalyticsEvent1(val name: String, val value: String)
data class Feat100AnalyticsEvent2(val name: String, val value: String)
data class Feat100AnalyticsEvent3(val name: String, val value: String)
data class Feat100AnalyticsEvent4(val name: String, val value: String)
data class Feat100AnalyticsEvent5(val name: String, val value: String)
data class Feat100AnalyticsEvent6(val name: String, val value: String)
data class Feat100AnalyticsEvent7(val name: String, val value: String)
data class Feat100AnalyticsEvent8(val name: String, val value: String)
data class Feat100AnalyticsEvent9(val name: String, val value: String)
data class Feat100AnalyticsEvent10(val name: String, val value: String)

fun logFeat100Event1(event: Feat100AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat100Event2(event: Feat100AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat100Event3(event: Feat100AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat100Event4(event: Feat100AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat100Event5(event: Feat100AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat100Event6(event: Feat100AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat100Event7(event: Feat100AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat100Event8(event: Feat100AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat100Event9(event: Feat100AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat100Event10(event: Feat100AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat100Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat100Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat100(u: CoreUser): Feat100Projection1 =
    Feat100Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat100Projection1> {
    val list = java.util.ArrayList<Feat100Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat100(u)
    }
    return list
}
