package com.romix.feature.feat307

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat307Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat307UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat307FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat307UserSummary
)

data class Feat307UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat307NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat307Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat307Config = Feat307Config()
) {

    fun loadSnapshot(userId: Long): Feat307NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat307NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat307UserSummary {
        return Feat307UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat307FeedItem> {
        val result = java.util.ArrayList<Feat307FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat307FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat307UiMapper {

    fun mapToUi(model: List<Feat307FeedItem>): Feat307UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat307UiModel(
            header = UiText("Feat307 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat307UiModel =
        Feat307UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat307UiModel =
        Feat307UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat307UiModel =
        Feat307UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat307Service(
    private val repository: Feat307Repository,
    private val uiMapper: Feat307UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat307UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat307UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat307UserItem1(val user: CoreUser, val label: String)
data class Feat307UserItem2(val user: CoreUser, val label: String)
data class Feat307UserItem3(val user: CoreUser, val label: String)
data class Feat307UserItem4(val user: CoreUser, val label: String)
data class Feat307UserItem5(val user: CoreUser, val label: String)
data class Feat307UserItem6(val user: CoreUser, val label: String)
data class Feat307UserItem7(val user: CoreUser, val label: String)
data class Feat307UserItem8(val user: CoreUser, val label: String)
data class Feat307UserItem9(val user: CoreUser, val label: String)
data class Feat307UserItem10(val user: CoreUser, val label: String)

data class Feat307StateBlock1(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock2(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock3(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock4(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock5(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock6(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock7(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock8(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock9(val state: Feat307UiModel, val checksum: Int)
data class Feat307StateBlock10(val state: Feat307UiModel, val checksum: Int)

fun buildFeat307UserItem(user: CoreUser, index: Int): Feat307UserItem1 {
    return Feat307UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat307StateBlock(model: Feat307UiModel): Feat307StateBlock1 {
    return Feat307StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat307UserSummary> {
    val list = java.util.ArrayList<Feat307UserSummary>(users.size)
    for (user in users) {
        list += Feat307UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat307UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat307UiModel {
    val summaries = (0 until count).map {
        Feat307UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat307UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat307UiModel> {
    val models = java.util.ArrayList<Feat307UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat307AnalyticsEvent1(val name: String, val value: String)
data class Feat307AnalyticsEvent2(val name: String, val value: String)
data class Feat307AnalyticsEvent3(val name: String, val value: String)
data class Feat307AnalyticsEvent4(val name: String, val value: String)
data class Feat307AnalyticsEvent5(val name: String, val value: String)
data class Feat307AnalyticsEvent6(val name: String, val value: String)
data class Feat307AnalyticsEvent7(val name: String, val value: String)
data class Feat307AnalyticsEvent8(val name: String, val value: String)
data class Feat307AnalyticsEvent9(val name: String, val value: String)
data class Feat307AnalyticsEvent10(val name: String, val value: String)

fun logFeat307Event1(event: Feat307AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat307Event2(event: Feat307AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat307Event3(event: Feat307AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat307Event4(event: Feat307AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat307Event5(event: Feat307AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat307Event6(event: Feat307AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat307Event7(event: Feat307AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat307Event8(event: Feat307AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat307Event9(event: Feat307AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat307Event10(event: Feat307AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat307Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat307Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat307(u: CoreUser): Feat307Projection1 =
    Feat307Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat307Projection1> {
    val list = java.util.ArrayList<Feat307Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat307(u)
    }
    return list
}
