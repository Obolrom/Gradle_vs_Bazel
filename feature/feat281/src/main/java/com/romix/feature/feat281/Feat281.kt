package com.romix.feature.feat281

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat281Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat281UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat281FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat281UserSummary
)

data class Feat281UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat281NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat281Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat281Config = Feat281Config()
) {

    fun loadSnapshot(userId: Long): Feat281NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat281NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat281UserSummary {
        return Feat281UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat281FeedItem> {
        val result = java.util.ArrayList<Feat281FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat281FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat281UiMapper {

    fun mapToUi(model: List<Feat281FeedItem>): Feat281UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat281UiModel(
            header = UiText("Feat281 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat281UiModel =
        Feat281UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat281UiModel =
        Feat281UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat281UiModel =
        Feat281UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat281Service(
    private val repository: Feat281Repository,
    private val uiMapper: Feat281UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat281UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat281UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat281UserItem1(val user: CoreUser, val label: String)
data class Feat281UserItem2(val user: CoreUser, val label: String)
data class Feat281UserItem3(val user: CoreUser, val label: String)
data class Feat281UserItem4(val user: CoreUser, val label: String)
data class Feat281UserItem5(val user: CoreUser, val label: String)
data class Feat281UserItem6(val user: CoreUser, val label: String)
data class Feat281UserItem7(val user: CoreUser, val label: String)
data class Feat281UserItem8(val user: CoreUser, val label: String)
data class Feat281UserItem9(val user: CoreUser, val label: String)
data class Feat281UserItem10(val user: CoreUser, val label: String)

data class Feat281StateBlock1(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock2(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock3(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock4(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock5(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock6(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock7(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock8(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock9(val state: Feat281UiModel, val checksum: Int)
data class Feat281StateBlock10(val state: Feat281UiModel, val checksum: Int)

fun buildFeat281UserItem(user: CoreUser, index: Int): Feat281UserItem1 {
    return Feat281UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat281StateBlock(model: Feat281UiModel): Feat281StateBlock1 {
    return Feat281StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat281UserSummary> {
    val list = java.util.ArrayList<Feat281UserSummary>(users.size)
    for (user in users) {
        list += Feat281UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat281UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat281UiModel {
    val summaries = (0 until count).map {
        Feat281UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat281UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat281UiModel> {
    val models = java.util.ArrayList<Feat281UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat281AnalyticsEvent1(val name: String, val value: String)
data class Feat281AnalyticsEvent2(val name: String, val value: String)
data class Feat281AnalyticsEvent3(val name: String, val value: String)
data class Feat281AnalyticsEvent4(val name: String, val value: String)
data class Feat281AnalyticsEvent5(val name: String, val value: String)
data class Feat281AnalyticsEvent6(val name: String, val value: String)
data class Feat281AnalyticsEvent7(val name: String, val value: String)
data class Feat281AnalyticsEvent8(val name: String, val value: String)
data class Feat281AnalyticsEvent9(val name: String, val value: String)
data class Feat281AnalyticsEvent10(val name: String, val value: String)

fun logFeat281Event1(event: Feat281AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat281Event2(event: Feat281AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat281Event3(event: Feat281AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat281Event4(event: Feat281AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat281Event5(event: Feat281AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat281Event6(event: Feat281AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat281Event7(event: Feat281AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat281Event8(event: Feat281AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat281Event9(event: Feat281AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat281Event10(event: Feat281AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat281Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat281Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat281(u: CoreUser): Feat281Projection1 =
    Feat281Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat281Projection1> {
    val list = java.util.ArrayList<Feat281Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat281(u)
    }
    return list
}
