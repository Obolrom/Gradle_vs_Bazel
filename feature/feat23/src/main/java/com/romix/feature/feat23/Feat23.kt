package com.romix.feature.feat23

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat23Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat23UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat23FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat23UserSummary
)

data class Feat23UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat23NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat23Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat23Config = Feat23Config()
) {

    fun loadSnapshot(userId: Long): Feat23NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat23NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat23UserSummary {
        return Feat23UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat23FeedItem> {
        val result = java.util.ArrayList<Feat23FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat23FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat23UiMapper {

    fun mapToUi(model: List<Feat23FeedItem>): Feat23UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat23UiModel(
            header = UiText("Feat23 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat23UiModel =
        Feat23UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat23UiModel =
        Feat23UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat23UiModel =
        Feat23UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat23Service(
    private val repository: Feat23Repository,
    private val uiMapper: Feat23UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat23UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat23UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat23UserItem1(val user: CoreUser, val label: String)
data class Feat23UserItem2(val user: CoreUser, val label: String)
data class Feat23UserItem3(val user: CoreUser, val label: String)
data class Feat23UserItem4(val user: CoreUser, val label: String)
data class Feat23UserItem5(val user: CoreUser, val label: String)
data class Feat23UserItem6(val user: CoreUser, val label: String)
data class Feat23UserItem7(val user: CoreUser, val label: String)
data class Feat23UserItem8(val user: CoreUser, val label: String)
data class Feat23UserItem9(val user: CoreUser, val label: String)
data class Feat23UserItem10(val user: CoreUser, val label: String)

data class Feat23StateBlock1(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock2(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock3(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock4(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock5(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock6(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock7(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock8(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock9(val state: Feat23UiModel, val checksum: Int)
data class Feat23StateBlock10(val state: Feat23UiModel, val checksum: Int)

fun buildFeat23UserItem(user: CoreUser, index: Int): Feat23UserItem1 {
    return Feat23UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat23StateBlock(model: Feat23UiModel): Feat23StateBlock1 {
    return Feat23StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat23UserSummary> {
    val list = java.util.ArrayList<Feat23UserSummary>(users.size)
    for (user in users) {
        list += Feat23UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat23UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat23UiModel {
    val summaries = (0 until count).map {
        Feat23UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat23UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat23UiModel> {
    val models = java.util.ArrayList<Feat23UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat23AnalyticsEvent1(val name: String, val value: String)
data class Feat23AnalyticsEvent2(val name: String, val value: String)
data class Feat23AnalyticsEvent3(val name: String, val value: String)
data class Feat23AnalyticsEvent4(val name: String, val value: String)
data class Feat23AnalyticsEvent5(val name: String, val value: String)
data class Feat23AnalyticsEvent6(val name: String, val value: String)
data class Feat23AnalyticsEvent7(val name: String, val value: String)
data class Feat23AnalyticsEvent8(val name: String, val value: String)
data class Feat23AnalyticsEvent9(val name: String, val value: String)
data class Feat23AnalyticsEvent10(val name: String, val value: String)

fun logFeat23Event1(event: Feat23AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat23Event2(event: Feat23AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat23Event3(event: Feat23AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat23Event4(event: Feat23AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat23Event5(event: Feat23AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat23Event6(event: Feat23AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat23Event7(event: Feat23AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat23Event8(event: Feat23AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat23Event9(event: Feat23AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat23Event10(event: Feat23AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat23Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat23Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat23(u: CoreUser): Feat23Projection1 =
    Feat23Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat23Projection1> {
    val list = java.util.ArrayList<Feat23Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat23(u)
    }
    return list
}
