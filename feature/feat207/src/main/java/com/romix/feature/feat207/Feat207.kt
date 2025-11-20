package com.romix.feature.feat207

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat207Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat207UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat207FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat207UserSummary
)

data class Feat207UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat207NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat207Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat207Config = Feat207Config()
) {

    fun loadSnapshot(userId: Long): Feat207NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat207NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat207UserSummary {
        return Feat207UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat207FeedItem> {
        val result = java.util.ArrayList<Feat207FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat207FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat207UiMapper {

    fun mapToUi(model: List<Feat207FeedItem>): Feat207UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat207UiModel(
            header = UiText("Feat207 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat207UiModel =
        Feat207UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat207UiModel =
        Feat207UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat207UiModel =
        Feat207UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat207Service(
    private val repository: Feat207Repository,
    private val uiMapper: Feat207UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat207UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat207UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat207UserItem1(val user: CoreUser, val label: String)
data class Feat207UserItem2(val user: CoreUser, val label: String)
data class Feat207UserItem3(val user: CoreUser, val label: String)
data class Feat207UserItem4(val user: CoreUser, val label: String)
data class Feat207UserItem5(val user: CoreUser, val label: String)
data class Feat207UserItem6(val user: CoreUser, val label: String)
data class Feat207UserItem7(val user: CoreUser, val label: String)
data class Feat207UserItem8(val user: CoreUser, val label: String)
data class Feat207UserItem9(val user: CoreUser, val label: String)
data class Feat207UserItem10(val user: CoreUser, val label: String)

data class Feat207StateBlock1(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock2(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock3(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock4(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock5(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock6(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock7(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock8(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock9(val state: Feat207UiModel, val checksum: Int)
data class Feat207StateBlock10(val state: Feat207UiModel, val checksum: Int)

fun buildFeat207UserItem(user: CoreUser, index: Int): Feat207UserItem1 {
    return Feat207UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat207StateBlock(model: Feat207UiModel): Feat207StateBlock1 {
    return Feat207StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat207UserSummary> {
    val list = java.util.ArrayList<Feat207UserSummary>(users.size)
    for (user in users) {
        list += Feat207UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat207UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat207UiModel {
    val summaries = (0 until count).map {
        Feat207UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat207UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat207UiModel> {
    val models = java.util.ArrayList<Feat207UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat207AnalyticsEvent1(val name: String, val value: String)
data class Feat207AnalyticsEvent2(val name: String, val value: String)
data class Feat207AnalyticsEvent3(val name: String, val value: String)
data class Feat207AnalyticsEvent4(val name: String, val value: String)
data class Feat207AnalyticsEvent5(val name: String, val value: String)
data class Feat207AnalyticsEvent6(val name: String, val value: String)
data class Feat207AnalyticsEvent7(val name: String, val value: String)
data class Feat207AnalyticsEvent8(val name: String, val value: String)
data class Feat207AnalyticsEvent9(val name: String, val value: String)
data class Feat207AnalyticsEvent10(val name: String, val value: String)

fun logFeat207Event1(event: Feat207AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat207Event2(event: Feat207AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat207Event3(event: Feat207AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat207Event4(event: Feat207AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat207Event5(event: Feat207AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat207Event6(event: Feat207AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat207Event7(event: Feat207AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat207Event8(event: Feat207AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat207Event9(event: Feat207AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat207Event10(event: Feat207AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat207Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat207Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat207(u: CoreUser): Feat207Projection1 =
    Feat207Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat207Projection1> {
    val list = java.util.ArrayList<Feat207Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat207(u)
    }
    return list
}
