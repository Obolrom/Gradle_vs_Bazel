package com.romix.feature.feat292

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat292Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat292UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat292FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat292UserSummary
)

data class Feat292UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat292NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat292Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat292Config = Feat292Config()
) {

    fun loadSnapshot(userId: Long): Feat292NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat292NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat292UserSummary {
        return Feat292UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat292FeedItem> {
        val result = java.util.ArrayList<Feat292FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat292FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat292UiMapper {

    fun mapToUi(model: List<Feat292FeedItem>): Feat292UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat292UiModel(
            header = UiText("Feat292 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat292UiModel =
        Feat292UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat292UiModel =
        Feat292UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat292UiModel =
        Feat292UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat292Service(
    private val repository: Feat292Repository,
    private val uiMapper: Feat292UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat292UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat292UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat292UserItem1(val user: CoreUser, val label: String)
data class Feat292UserItem2(val user: CoreUser, val label: String)
data class Feat292UserItem3(val user: CoreUser, val label: String)
data class Feat292UserItem4(val user: CoreUser, val label: String)
data class Feat292UserItem5(val user: CoreUser, val label: String)
data class Feat292UserItem6(val user: CoreUser, val label: String)
data class Feat292UserItem7(val user: CoreUser, val label: String)
data class Feat292UserItem8(val user: CoreUser, val label: String)
data class Feat292UserItem9(val user: CoreUser, val label: String)
data class Feat292UserItem10(val user: CoreUser, val label: String)

data class Feat292StateBlock1(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock2(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock3(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock4(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock5(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock6(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock7(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock8(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock9(val state: Feat292UiModel, val checksum: Int)
data class Feat292StateBlock10(val state: Feat292UiModel, val checksum: Int)

fun buildFeat292UserItem(user: CoreUser, index: Int): Feat292UserItem1 {
    return Feat292UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat292StateBlock(model: Feat292UiModel): Feat292StateBlock1 {
    return Feat292StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat292UserSummary> {
    val list = java.util.ArrayList<Feat292UserSummary>(users.size)
    for (user in users) {
        list += Feat292UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat292UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat292UiModel {
    val summaries = (0 until count).map {
        Feat292UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat292UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat292UiModel> {
    val models = java.util.ArrayList<Feat292UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat292AnalyticsEvent1(val name: String, val value: String)
data class Feat292AnalyticsEvent2(val name: String, val value: String)
data class Feat292AnalyticsEvent3(val name: String, val value: String)
data class Feat292AnalyticsEvent4(val name: String, val value: String)
data class Feat292AnalyticsEvent5(val name: String, val value: String)
data class Feat292AnalyticsEvent6(val name: String, val value: String)
data class Feat292AnalyticsEvent7(val name: String, val value: String)
data class Feat292AnalyticsEvent8(val name: String, val value: String)
data class Feat292AnalyticsEvent9(val name: String, val value: String)
data class Feat292AnalyticsEvent10(val name: String, val value: String)

fun logFeat292Event1(event: Feat292AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat292Event2(event: Feat292AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat292Event3(event: Feat292AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat292Event4(event: Feat292AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat292Event5(event: Feat292AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat292Event6(event: Feat292AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat292Event7(event: Feat292AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat292Event8(event: Feat292AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat292Event9(event: Feat292AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat292Event10(event: Feat292AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat292Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat292Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat292(u: CoreUser): Feat292Projection1 =
    Feat292Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat292Projection1> {
    val list = java.util.ArrayList<Feat292Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat292(u)
    }
    return list
}
