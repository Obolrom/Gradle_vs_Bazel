package com.romix.feature.feat375

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat375Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat375UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat375FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat375UserSummary
)

data class Feat375UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat375NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat375Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat375Config = Feat375Config()
) {

    fun loadSnapshot(userId: Long): Feat375NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat375NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat375UserSummary {
        return Feat375UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat375FeedItem> {
        val result = java.util.ArrayList<Feat375FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat375FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat375UiMapper {

    fun mapToUi(model: List<Feat375FeedItem>): Feat375UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat375UiModel(
            header = UiText("Feat375 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat375UiModel =
        Feat375UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat375UiModel =
        Feat375UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat375UiModel =
        Feat375UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat375Service(
    private val repository: Feat375Repository,
    private val uiMapper: Feat375UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat375UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat375UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat375UserItem1(val user: CoreUser, val label: String)
data class Feat375UserItem2(val user: CoreUser, val label: String)
data class Feat375UserItem3(val user: CoreUser, val label: String)
data class Feat375UserItem4(val user: CoreUser, val label: String)
data class Feat375UserItem5(val user: CoreUser, val label: String)
data class Feat375UserItem6(val user: CoreUser, val label: String)
data class Feat375UserItem7(val user: CoreUser, val label: String)
data class Feat375UserItem8(val user: CoreUser, val label: String)
data class Feat375UserItem9(val user: CoreUser, val label: String)
data class Feat375UserItem10(val user: CoreUser, val label: String)

data class Feat375StateBlock1(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock2(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock3(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock4(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock5(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock6(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock7(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock8(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock9(val state: Feat375UiModel, val checksum: Int)
data class Feat375StateBlock10(val state: Feat375UiModel, val checksum: Int)

fun buildFeat375UserItem(user: CoreUser, index: Int): Feat375UserItem1 {
    return Feat375UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat375StateBlock(model: Feat375UiModel): Feat375StateBlock1 {
    return Feat375StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat375UserSummary> {
    val list = java.util.ArrayList<Feat375UserSummary>(users.size)
    for (user in users) {
        list += Feat375UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat375UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat375UiModel {
    val summaries = (0 until count).map {
        Feat375UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat375UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat375UiModel> {
    val models = java.util.ArrayList<Feat375UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat375AnalyticsEvent1(val name: String, val value: String)
data class Feat375AnalyticsEvent2(val name: String, val value: String)
data class Feat375AnalyticsEvent3(val name: String, val value: String)
data class Feat375AnalyticsEvent4(val name: String, val value: String)
data class Feat375AnalyticsEvent5(val name: String, val value: String)
data class Feat375AnalyticsEvent6(val name: String, val value: String)
data class Feat375AnalyticsEvent7(val name: String, val value: String)
data class Feat375AnalyticsEvent8(val name: String, val value: String)
data class Feat375AnalyticsEvent9(val name: String, val value: String)
data class Feat375AnalyticsEvent10(val name: String, val value: String)

fun logFeat375Event1(event: Feat375AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat375Event2(event: Feat375AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat375Event3(event: Feat375AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat375Event4(event: Feat375AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat375Event5(event: Feat375AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat375Event6(event: Feat375AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat375Event7(event: Feat375AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat375Event8(event: Feat375AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat375Event9(event: Feat375AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat375Event10(event: Feat375AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat375Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat375Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat375(u: CoreUser): Feat375Projection1 =
    Feat375Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat375Projection1> {
    val list = java.util.ArrayList<Feat375Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat375(u)
    }
    return list
}
