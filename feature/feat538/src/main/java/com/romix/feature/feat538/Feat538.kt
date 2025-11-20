package com.romix.feature.feat538

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat538Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat538UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat538FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat538UserSummary
)

data class Feat538UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat538NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat538Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat538Config = Feat538Config()
) {

    fun loadSnapshot(userId: Long): Feat538NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat538NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat538UserSummary {
        return Feat538UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat538FeedItem> {
        val result = java.util.ArrayList<Feat538FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat538FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat538UiMapper {

    fun mapToUi(model: List<Feat538FeedItem>): Feat538UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat538UiModel(
            header = UiText("Feat538 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat538UiModel =
        Feat538UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat538UiModel =
        Feat538UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat538UiModel =
        Feat538UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat538Service(
    private val repository: Feat538Repository,
    private val uiMapper: Feat538UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat538UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat538UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat538UserItem1(val user: CoreUser, val label: String)
data class Feat538UserItem2(val user: CoreUser, val label: String)
data class Feat538UserItem3(val user: CoreUser, val label: String)
data class Feat538UserItem4(val user: CoreUser, val label: String)
data class Feat538UserItem5(val user: CoreUser, val label: String)
data class Feat538UserItem6(val user: CoreUser, val label: String)
data class Feat538UserItem7(val user: CoreUser, val label: String)
data class Feat538UserItem8(val user: CoreUser, val label: String)
data class Feat538UserItem9(val user: CoreUser, val label: String)
data class Feat538UserItem10(val user: CoreUser, val label: String)

data class Feat538StateBlock1(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock2(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock3(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock4(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock5(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock6(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock7(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock8(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock9(val state: Feat538UiModel, val checksum: Int)
data class Feat538StateBlock10(val state: Feat538UiModel, val checksum: Int)

fun buildFeat538UserItem(user: CoreUser, index: Int): Feat538UserItem1 {
    return Feat538UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat538StateBlock(model: Feat538UiModel): Feat538StateBlock1 {
    return Feat538StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat538UserSummary> {
    val list = java.util.ArrayList<Feat538UserSummary>(users.size)
    for (user in users) {
        list += Feat538UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat538UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat538UiModel {
    val summaries = (0 until count).map {
        Feat538UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat538UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat538UiModel> {
    val models = java.util.ArrayList<Feat538UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat538AnalyticsEvent1(val name: String, val value: String)
data class Feat538AnalyticsEvent2(val name: String, val value: String)
data class Feat538AnalyticsEvent3(val name: String, val value: String)
data class Feat538AnalyticsEvent4(val name: String, val value: String)
data class Feat538AnalyticsEvent5(val name: String, val value: String)
data class Feat538AnalyticsEvent6(val name: String, val value: String)
data class Feat538AnalyticsEvent7(val name: String, val value: String)
data class Feat538AnalyticsEvent8(val name: String, val value: String)
data class Feat538AnalyticsEvent9(val name: String, val value: String)
data class Feat538AnalyticsEvent10(val name: String, val value: String)

fun logFeat538Event1(event: Feat538AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat538Event2(event: Feat538AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat538Event3(event: Feat538AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat538Event4(event: Feat538AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat538Event5(event: Feat538AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat538Event6(event: Feat538AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat538Event7(event: Feat538AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat538Event8(event: Feat538AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat538Event9(event: Feat538AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat538Event10(event: Feat538AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat538Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat538Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat538(u: CoreUser): Feat538Projection1 =
    Feat538Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat538Projection1> {
    val list = java.util.ArrayList<Feat538Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat538(u)
    }
    return list
}
