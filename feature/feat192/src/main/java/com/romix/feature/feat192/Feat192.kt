package com.romix.feature.feat192

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat192Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat192UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat192FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat192UserSummary
)

data class Feat192UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat192NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat192Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat192Config = Feat192Config()
) {

    fun loadSnapshot(userId: Long): Feat192NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat192NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat192UserSummary {
        return Feat192UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat192FeedItem> {
        val result = java.util.ArrayList<Feat192FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat192FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat192UiMapper {

    fun mapToUi(model: List<Feat192FeedItem>): Feat192UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat192UiModel(
            header = UiText("Feat192 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat192UiModel =
        Feat192UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat192UiModel =
        Feat192UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat192UiModel =
        Feat192UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat192Service(
    private val repository: Feat192Repository,
    private val uiMapper: Feat192UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat192UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat192UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat192UserItem1(val user: CoreUser, val label: String)
data class Feat192UserItem2(val user: CoreUser, val label: String)
data class Feat192UserItem3(val user: CoreUser, val label: String)
data class Feat192UserItem4(val user: CoreUser, val label: String)
data class Feat192UserItem5(val user: CoreUser, val label: String)
data class Feat192UserItem6(val user: CoreUser, val label: String)
data class Feat192UserItem7(val user: CoreUser, val label: String)
data class Feat192UserItem8(val user: CoreUser, val label: String)
data class Feat192UserItem9(val user: CoreUser, val label: String)
data class Feat192UserItem10(val user: CoreUser, val label: String)

data class Feat192StateBlock1(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock2(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock3(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock4(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock5(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock6(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock7(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock8(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock9(val state: Feat192UiModel, val checksum: Int)
data class Feat192StateBlock10(val state: Feat192UiModel, val checksum: Int)

fun buildFeat192UserItem(user: CoreUser, index: Int): Feat192UserItem1 {
    return Feat192UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat192StateBlock(model: Feat192UiModel): Feat192StateBlock1 {
    return Feat192StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat192UserSummary> {
    val list = java.util.ArrayList<Feat192UserSummary>(users.size)
    for (user in users) {
        list += Feat192UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat192UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat192UiModel {
    val summaries = (0 until count).map {
        Feat192UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat192UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat192UiModel> {
    val models = java.util.ArrayList<Feat192UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat192AnalyticsEvent1(val name: String, val value: String)
data class Feat192AnalyticsEvent2(val name: String, val value: String)
data class Feat192AnalyticsEvent3(val name: String, val value: String)
data class Feat192AnalyticsEvent4(val name: String, val value: String)
data class Feat192AnalyticsEvent5(val name: String, val value: String)
data class Feat192AnalyticsEvent6(val name: String, val value: String)
data class Feat192AnalyticsEvent7(val name: String, val value: String)
data class Feat192AnalyticsEvent8(val name: String, val value: String)
data class Feat192AnalyticsEvent9(val name: String, val value: String)
data class Feat192AnalyticsEvent10(val name: String, val value: String)

fun logFeat192Event1(event: Feat192AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat192Event2(event: Feat192AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat192Event3(event: Feat192AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat192Event4(event: Feat192AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat192Event5(event: Feat192AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat192Event6(event: Feat192AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat192Event7(event: Feat192AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat192Event8(event: Feat192AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat192Event9(event: Feat192AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat192Event10(event: Feat192AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat192Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat192Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat192(u: CoreUser): Feat192Projection1 =
    Feat192Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat192Projection1> {
    val list = java.util.ArrayList<Feat192Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat192(u)
    }
    return list
}
