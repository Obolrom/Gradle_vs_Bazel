package com.romix.feature.feat255

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat255Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat255UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat255FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat255UserSummary
)

data class Feat255UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat255NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat255Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat255Config = Feat255Config()
) {

    fun loadSnapshot(userId: Long): Feat255NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat255NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat255UserSummary {
        return Feat255UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat255FeedItem> {
        val result = java.util.ArrayList<Feat255FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat255FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat255UiMapper {

    fun mapToUi(model: List<Feat255FeedItem>): Feat255UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat255UiModel(
            header = UiText("Feat255 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat255UiModel =
        Feat255UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat255UiModel =
        Feat255UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat255UiModel =
        Feat255UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat255Service(
    private val repository: Feat255Repository,
    private val uiMapper: Feat255UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat255UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat255UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat255UserItem1(val user: CoreUser, val label: String)
data class Feat255UserItem2(val user: CoreUser, val label: String)
data class Feat255UserItem3(val user: CoreUser, val label: String)
data class Feat255UserItem4(val user: CoreUser, val label: String)
data class Feat255UserItem5(val user: CoreUser, val label: String)
data class Feat255UserItem6(val user: CoreUser, val label: String)
data class Feat255UserItem7(val user: CoreUser, val label: String)
data class Feat255UserItem8(val user: CoreUser, val label: String)
data class Feat255UserItem9(val user: CoreUser, val label: String)
data class Feat255UserItem10(val user: CoreUser, val label: String)

data class Feat255StateBlock1(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock2(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock3(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock4(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock5(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock6(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock7(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock8(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock9(val state: Feat255UiModel, val checksum: Int)
data class Feat255StateBlock10(val state: Feat255UiModel, val checksum: Int)

fun buildFeat255UserItem(user: CoreUser, index: Int): Feat255UserItem1 {
    return Feat255UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat255StateBlock(model: Feat255UiModel): Feat255StateBlock1 {
    return Feat255StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat255UserSummary> {
    val list = java.util.ArrayList<Feat255UserSummary>(users.size)
    for (user in users) {
        list += Feat255UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat255UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat255UiModel {
    val summaries = (0 until count).map {
        Feat255UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat255UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat255UiModel> {
    val models = java.util.ArrayList<Feat255UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat255AnalyticsEvent1(val name: String, val value: String)
data class Feat255AnalyticsEvent2(val name: String, val value: String)
data class Feat255AnalyticsEvent3(val name: String, val value: String)
data class Feat255AnalyticsEvent4(val name: String, val value: String)
data class Feat255AnalyticsEvent5(val name: String, val value: String)
data class Feat255AnalyticsEvent6(val name: String, val value: String)
data class Feat255AnalyticsEvent7(val name: String, val value: String)
data class Feat255AnalyticsEvent8(val name: String, val value: String)
data class Feat255AnalyticsEvent9(val name: String, val value: String)
data class Feat255AnalyticsEvent10(val name: String, val value: String)

fun logFeat255Event1(event: Feat255AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat255Event2(event: Feat255AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat255Event3(event: Feat255AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat255Event4(event: Feat255AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat255Event5(event: Feat255AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat255Event6(event: Feat255AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat255Event7(event: Feat255AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat255Event8(event: Feat255AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat255Event9(event: Feat255AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat255Event10(event: Feat255AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat255Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat255Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat255(u: CoreUser): Feat255Projection1 =
    Feat255Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat255Projection1> {
    val list = java.util.ArrayList<Feat255Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat255(u)
    }
    return list
}
