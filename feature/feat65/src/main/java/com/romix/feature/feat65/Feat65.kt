package com.romix.feature.feat65

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat65Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat65UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat65FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat65UserSummary
)

data class Feat65UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat65NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat65Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat65Config = Feat65Config()
) {

    fun loadSnapshot(userId: Long): Feat65NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat65NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat65UserSummary {
        return Feat65UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat65FeedItem> {
        val result = java.util.ArrayList<Feat65FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat65FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat65UiMapper {

    fun mapToUi(model: List<Feat65FeedItem>): Feat65UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat65UiModel(
            header = UiText("Feat65 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat65UiModel =
        Feat65UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat65UiModel =
        Feat65UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat65UiModel =
        Feat65UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat65Service(
    private val repository: Feat65Repository,
    private val uiMapper: Feat65UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat65UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat65UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat65UserItem1(val user: CoreUser, val label: String)
data class Feat65UserItem2(val user: CoreUser, val label: String)
data class Feat65UserItem3(val user: CoreUser, val label: String)
data class Feat65UserItem4(val user: CoreUser, val label: String)
data class Feat65UserItem5(val user: CoreUser, val label: String)
data class Feat65UserItem6(val user: CoreUser, val label: String)
data class Feat65UserItem7(val user: CoreUser, val label: String)
data class Feat65UserItem8(val user: CoreUser, val label: String)
data class Feat65UserItem9(val user: CoreUser, val label: String)
data class Feat65UserItem10(val user: CoreUser, val label: String)

data class Feat65StateBlock1(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock2(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock3(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock4(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock5(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock6(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock7(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock8(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock9(val state: Feat65UiModel, val checksum: Int)
data class Feat65StateBlock10(val state: Feat65UiModel, val checksum: Int)

fun buildFeat65UserItem(user: CoreUser, index: Int): Feat65UserItem1 {
    return Feat65UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat65StateBlock(model: Feat65UiModel): Feat65StateBlock1 {
    return Feat65StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat65UserSummary> {
    val list = java.util.ArrayList<Feat65UserSummary>(users.size)
    for (user in users) {
        list += Feat65UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat65UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat65UiModel {
    val summaries = (0 until count).map {
        Feat65UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat65UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat65UiModel> {
    val models = java.util.ArrayList<Feat65UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat65AnalyticsEvent1(val name: String, val value: String)
data class Feat65AnalyticsEvent2(val name: String, val value: String)
data class Feat65AnalyticsEvent3(val name: String, val value: String)
data class Feat65AnalyticsEvent4(val name: String, val value: String)
data class Feat65AnalyticsEvent5(val name: String, val value: String)
data class Feat65AnalyticsEvent6(val name: String, val value: String)
data class Feat65AnalyticsEvent7(val name: String, val value: String)
data class Feat65AnalyticsEvent8(val name: String, val value: String)
data class Feat65AnalyticsEvent9(val name: String, val value: String)
data class Feat65AnalyticsEvent10(val name: String, val value: String)

fun logFeat65Event1(event: Feat65AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat65Event2(event: Feat65AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat65Event3(event: Feat65AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat65Event4(event: Feat65AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat65Event5(event: Feat65AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat65Event6(event: Feat65AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat65Event7(event: Feat65AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat65Event8(event: Feat65AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat65Event9(event: Feat65AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat65Event10(event: Feat65AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat65Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat65Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat65(u: CoreUser): Feat65Projection1 =
    Feat65Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat65Projection1> {
    val list = java.util.ArrayList<Feat65Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat65(u)
    }
    return list
}
