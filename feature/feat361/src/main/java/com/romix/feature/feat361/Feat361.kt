package com.romix.feature.feat361

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat361Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat361UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat361FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat361UserSummary
)

data class Feat361UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat361NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat361Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat361Config = Feat361Config()
) {

    fun loadSnapshot(userId: Long): Feat361NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat361NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat361UserSummary {
        return Feat361UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat361FeedItem> {
        val result = java.util.ArrayList<Feat361FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat361FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat361UiMapper {

    fun mapToUi(model: List<Feat361FeedItem>): Feat361UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat361UiModel(
            header = UiText("Feat361 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat361UiModel =
        Feat361UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat361UiModel =
        Feat361UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat361UiModel =
        Feat361UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat361Service(
    private val repository: Feat361Repository,
    private val uiMapper: Feat361UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat361UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat361UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat361UserItem1(val user: CoreUser, val label: String)
data class Feat361UserItem2(val user: CoreUser, val label: String)
data class Feat361UserItem3(val user: CoreUser, val label: String)
data class Feat361UserItem4(val user: CoreUser, val label: String)
data class Feat361UserItem5(val user: CoreUser, val label: String)
data class Feat361UserItem6(val user: CoreUser, val label: String)
data class Feat361UserItem7(val user: CoreUser, val label: String)
data class Feat361UserItem8(val user: CoreUser, val label: String)
data class Feat361UserItem9(val user: CoreUser, val label: String)
data class Feat361UserItem10(val user: CoreUser, val label: String)

data class Feat361StateBlock1(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock2(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock3(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock4(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock5(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock6(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock7(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock8(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock9(val state: Feat361UiModel, val checksum: Int)
data class Feat361StateBlock10(val state: Feat361UiModel, val checksum: Int)

fun buildFeat361UserItem(user: CoreUser, index: Int): Feat361UserItem1 {
    return Feat361UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat361StateBlock(model: Feat361UiModel): Feat361StateBlock1 {
    return Feat361StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat361UserSummary> {
    val list = java.util.ArrayList<Feat361UserSummary>(users.size)
    for (user in users) {
        list += Feat361UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat361UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat361UiModel {
    val summaries = (0 until count).map {
        Feat361UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat361UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat361UiModel> {
    val models = java.util.ArrayList<Feat361UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat361AnalyticsEvent1(val name: String, val value: String)
data class Feat361AnalyticsEvent2(val name: String, val value: String)
data class Feat361AnalyticsEvent3(val name: String, val value: String)
data class Feat361AnalyticsEvent4(val name: String, val value: String)
data class Feat361AnalyticsEvent5(val name: String, val value: String)
data class Feat361AnalyticsEvent6(val name: String, val value: String)
data class Feat361AnalyticsEvent7(val name: String, val value: String)
data class Feat361AnalyticsEvent8(val name: String, val value: String)
data class Feat361AnalyticsEvent9(val name: String, val value: String)
data class Feat361AnalyticsEvent10(val name: String, val value: String)

fun logFeat361Event1(event: Feat361AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat361Event2(event: Feat361AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat361Event3(event: Feat361AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat361Event4(event: Feat361AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat361Event5(event: Feat361AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat361Event6(event: Feat361AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat361Event7(event: Feat361AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat361Event8(event: Feat361AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat361Event9(event: Feat361AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat361Event10(event: Feat361AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat361Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat361Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat361(u: CoreUser): Feat361Projection1 =
    Feat361Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat361Projection1> {
    val list = java.util.ArrayList<Feat361Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat361(u)
    }
    return list
}
