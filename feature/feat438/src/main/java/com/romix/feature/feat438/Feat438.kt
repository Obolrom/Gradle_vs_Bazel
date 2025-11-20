package com.romix.feature.feat438

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat438Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat438UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat438FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat438UserSummary
)

data class Feat438UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat438NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat438Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat438Config = Feat438Config()
) {

    fun loadSnapshot(userId: Long): Feat438NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat438NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat438UserSummary {
        return Feat438UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat438FeedItem> {
        val result = java.util.ArrayList<Feat438FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat438FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat438UiMapper {

    fun mapToUi(model: List<Feat438FeedItem>): Feat438UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat438UiModel(
            header = UiText("Feat438 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat438UiModel =
        Feat438UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat438UiModel =
        Feat438UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat438UiModel =
        Feat438UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat438Service(
    private val repository: Feat438Repository,
    private val uiMapper: Feat438UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat438UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat438UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat438UserItem1(val user: CoreUser, val label: String)
data class Feat438UserItem2(val user: CoreUser, val label: String)
data class Feat438UserItem3(val user: CoreUser, val label: String)
data class Feat438UserItem4(val user: CoreUser, val label: String)
data class Feat438UserItem5(val user: CoreUser, val label: String)
data class Feat438UserItem6(val user: CoreUser, val label: String)
data class Feat438UserItem7(val user: CoreUser, val label: String)
data class Feat438UserItem8(val user: CoreUser, val label: String)
data class Feat438UserItem9(val user: CoreUser, val label: String)
data class Feat438UserItem10(val user: CoreUser, val label: String)

data class Feat438StateBlock1(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock2(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock3(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock4(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock5(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock6(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock7(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock8(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock9(val state: Feat438UiModel, val checksum: Int)
data class Feat438StateBlock10(val state: Feat438UiModel, val checksum: Int)

fun buildFeat438UserItem(user: CoreUser, index: Int): Feat438UserItem1 {
    return Feat438UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat438StateBlock(model: Feat438UiModel): Feat438StateBlock1 {
    return Feat438StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat438UserSummary> {
    val list = java.util.ArrayList<Feat438UserSummary>(users.size)
    for (user in users) {
        list += Feat438UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat438UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat438UiModel {
    val summaries = (0 until count).map {
        Feat438UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat438UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat438UiModel> {
    val models = java.util.ArrayList<Feat438UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat438AnalyticsEvent1(val name: String, val value: String)
data class Feat438AnalyticsEvent2(val name: String, val value: String)
data class Feat438AnalyticsEvent3(val name: String, val value: String)
data class Feat438AnalyticsEvent4(val name: String, val value: String)
data class Feat438AnalyticsEvent5(val name: String, val value: String)
data class Feat438AnalyticsEvent6(val name: String, val value: String)
data class Feat438AnalyticsEvent7(val name: String, val value: String)
data class Feat438AnalyticsEvent8(val name: String, val value: String)
data class Feat438AnalyticsEvent9(val name: String, val value: String)
data class Feat438AnalyticsEvent10(val name: String, val value: String)

fun logFeat438Event1(event: Feat438AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat438Event2(event: Feat438AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat438Event3(event: Feat438AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat438Event4(event: Feat438AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat438Event5(event: Feat438AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat438Event6(event: Feat438AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat438Event7(event: Feat438AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat438Event8(event: Feat438AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat438Event9(event: Feat438AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat438Event10(event: Feat438AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat438Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat438Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat438(u: CoreUser): Feat438Projection1 =
    Feat438Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat438Projection1> {
    val list = java.util.ArrayList<Feat438Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat438(u)
    }
    return list
}
