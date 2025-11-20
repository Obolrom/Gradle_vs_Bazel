package com.romix.feature.feat296

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat296Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat296UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat296FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat296UserSummary
)

data class Feat296UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat296NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat296Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat296Config = Feat296Config()
) {

    fun loadSnapshot(userId: Long): Feat296NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat296NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat296UserSummary {
        return Feat296UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat296FeedItem> {
        val result = java.util.ArrayList<Feat296FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat296FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat296UiMapper {

    fun mapToUi(model: List<Feat296FeedItem>): Feat296UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat296UiModel(
            header = UiText("Feat296 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat296UiModel =
        Feat296UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat296UiModel =
        Feat296UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat296UiModel =
        Feat296UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat296Service(
    private val repository: Feat296Repository,
    private val uiMapper: Feat296UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat296UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat296UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat296UserItem1(val user: CoreUser, val label: String)
data class Feat296UserItem2(val user: CoreUser, val label: String)
data class Feat296UserItem3(val user: CoreUser, val label: String)
data class Feat296UserItem4(val user: CoreUser, val label: String)
data class Feat296UserItem5(val user: CoreUser, val label: String)
data class Feat296UserItem6(val user: CoreUser, val label: String)
data class Feat296UserItem7(val user: CoreUser, val label: String)
data class Feat296UserItem8(val user: CoreUser, val label: String)
data class Feat296UserItem9(val user: CoreUser, val label: String)
data class Feat296UserItem10(val user: CoreUser, val label: String)

data class Feat296StateBlock1(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock2(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock3(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock4(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock5(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock6(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock7(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock8(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock9(val state: Feat296UiModel, val checksum: Int)
data class Feat296StateBlock10(val state: Feat296UiModel, val checksum: Int)

fun buildFeat296UserItem(user: CoreUser, index: Int): Feat296UserItem1 {
    return Feat296UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat296StateBlock(model: Feat296UiModel): Feat296StateBlock1 {
    return Feat296StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat296UserSummary> {
    val list = java.util.ArrayList<Feat296UserSummary>(users.size)
    for (user in users) {
        list += Feat296UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat296UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat296UiModel {
    val summaries = (0 until count).map {
        Feat296UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat296UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat296UiModel> {
    val models = java.util.ArrayList<Feat296UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat296AnalyticsEvent1(val name: String, val value: String)
data class Feat296AnalyticsEvent2(val name: String, val value: String)
data class Feat296AnalyticsEvent3(val name: String, val value: String)
data class Feat296AnalyticsEvent4(val name: String, val value: String)
data class Feat296AnalyticsEvent5(val name: String, val value: String)
data class Feat296AnalyticsEvent6(val name: String, val value: String)
data class Feat296AnalyticsEvent7(val name: String, val value: String)
data class Feat296AnalyticsEvent8(val name: String, val value: String)
data class Feat296AnalyticsEvent9(val name: String, val value: String)
data class Feat296AnalyticsEvent10(val name: String, val value: String)

fun logFeat296Event1(event: Feat296AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat296Event2(event: Feat296AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat296Event3(event: Feat296AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat296Event4(event: Feat296AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat296Event5(event: Feat296AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat296Event6(event: Feat296AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat296Event7(event: Feat296AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat296Event8(event: Feat296AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat296Event9(event: Feat296AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat296Event10(event: Feat296AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat296Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat296Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat296(u: CoreUser): Feat296Projection1 =
    Feat296Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat296Projection1> {
    val list = java.util.ArrayList<Feat296Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat296(u)
    }
    return list
}
