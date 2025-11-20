package com.romix.feature.feat175

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat175Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat175UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat175FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat175UserSummary
)

data class Feat175UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat175NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat175Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat175Config = Feat175Config()
) {

    fun loadSnapshot(userId: Long): Feat175NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat175NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat175UserSummary {
        return Feat175UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat175FeedItem> {
        val result = java.util.ArrayList<Feat175FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat175FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat175UiMapper {

    fun mapToUi(model: List<Feat175FeedItem>): Feat175UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat175UiModel(
            header = UiText("Feat175 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat175UiModel =
        Feat175UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat175UiModel =
        Feat175UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat175UiModel =
        Feat175UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat175Service(
    private val repository: Feat175Repository,
    private val uiMapper: Feat175UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat175UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat175UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat175UserItem1(val user: CoreUser, val label: String)
data class Feat175UserItem2(val user: CoreUser, val label: String)
data class Feat175UserItem3(val user: CoreUser, val label: String)
data class Feat175UserItem4(val user: CoreUser, val label: String)
data class Feat175UserItem5(val user: CoreUser, val label: String)
data class Feat175UserItem6(val user: CoreUser, val label: String)
data class Feat175UserItem7(val user: CoreUser, val label: String)
data class Feat175UserItem8(val user: CoreUser, val label: String)
data class Feat175UserItem9(val user: CoreUser, val label: String)
data class Feat175UserItem10(val user: CoreUser, val label: String)

data class Feat175StateBlock1(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock2(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock3(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock4(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock5(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock6(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock7(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock8(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock9(val state: Feat175UiModel, val checksum: Int)
data class Feat175StateBlock10(val state: Feat175UiModel, val checksum: Int)

fun buildFeat175UserItem(user: CoreUser, index: Int): Feat175UserItem1 {
    return Feat175UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat175StateBlock(model: Feat175UiModel): Feat175StateBlock1 {
    return Feat175StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat175UserSummary> {
    val list = java.util.ArrayList<Feat175UserSummary>(users.size)
    for (user in users) {
        list += Feat175UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat175UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat175UiModel {
    val summaries = (0 until count).map {
        Feat175UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat175UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat175UiModel> {
    val models = java.util.ArrayList<Feat175UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat175AnalyticsEvent1(val name: String, val value: String)
data class Feat175AnalyticsEvent2(val name: String, val value: String)
data class Feat175AnalyticsEvent3(val name: String, val value: String)
data class Feat175AnalyticsEvent4(val name: String, val value: String)
data class Feat175AnalyticsEvent5(val name: String, val value: String)
data class Feat175AnalyticsEvent6(val name: String, val value: String)
data class Feat175AnalyticsEvent7(val name: String, val value: String)
data class Feat175AnalyticsEvent8(val name: String, val value: String)
data class Feat175AnalyticsEvent9(val name: String, val value: String)
data class Feat175AnalyticsEvent10(val name: String, val value: String)

fun logFeat175Event1(event: Feat175AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat175Event2(event: Feat175AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat175Event3(event: Feat175AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat175Event4(event: Feat175AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat175Event5(event: Feat175AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat175Event6(event: Feat175AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat175Event7(event: Feat175AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat175Event8(event: Feat175AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat175Event9(event: Feat175AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat175Event10(event: Feat175AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat175Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat175Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat175(u: CoreUser): Feat175Projection1 =
    Feat175Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat175Projection1> {
    val list = java.util.ArrayList<Feat175Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat175(u)
    }
    return list
}
