package com.romix.feature.feat348

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat348Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat348UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat348FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat348UserSummary
)

data class Feat348UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat348NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat348Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat348Config = Feat348Config()
) {

    fun loadSnapshot(userId: Long): Feat348NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat348NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat348UserSummary {
        return Feat348UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat348FeedItem> {
        val result = java.util.ArrayList<Feat348FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat348FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat348UiMapper {

    fun mapToUi(model: List<Feat348FeedItem>): Feat348UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat348UiModel(
            header = UiText("Feat348 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat348UiModel =
        Feat348UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat348UiModel =
        Feat348UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat348UiModel =
        Feat348UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat348Service(
    private val repository: Feat348Repository,
    private val uiMapper: Feat348UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat348UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat348UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat348UserItem1(val user: CoreUser, val label: String)
data class Feat348UserItem2(val user: CoreUser, val label: String)
data class Feat348UserItem3(val user: CoreUser, val label: String)
data class Feat348UserItem4(val user: CoreUser, val label: String)
data class Feat348UserItem5(val user: CoreUser, val label: String)
data class Feat348UserItem6(val user: CoreUser, val label: String)
data class Feat348UserItem7(val user: CoreUser, val label: String)
data class Feat348UserItem8(val user: CoreUser, val label: String)
data class Feat348UserItem9(val user: CoreUser, val label: String)
data class Feat348UserItem10(val user: CoreUser, val label: String)

data class Feat348StateBlock1(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock2(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock3(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock4(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock5(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock6(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock7(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock8(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock9(val state: Feat348UiModel, val checksum: Int)
data class Feat348StateBlock10(val state: Feat348UiModel, val checksum: Int)

fun buildFeat348UserItem(user: CoreUser, index: Int): Feat348UserItem1 {
    return Feat348UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat348StateBlock(model: Feat348UiModel): Feat348StateBlock1 {
    return Feat348StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat348UserSummary> {
    val list = java.util.ArrayList<Feat348UserSummary>(users.size)
    for (user in users) {
        list += Feat348UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat348UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat348UiModel {
    val summaries = (0 until count).map {
        Feat348UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat348UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat348UiModel> {
    val models = java.util.ArrayList<Feat348UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat348AnalyticsEvent1(val name: String, val value: String)
data class Feat348AnalyticsEvent2(val name: String, val value: String)
data class Feat348AnalyticsEvent3(val name: String, val value: String)
data class Feat348AnalyticsEvent4(val name: String, val value: String)
data class Feat348AnalyticsEvent5(val name: String, val value: String)
data class Feat348AnalyticsEvent6(val name: String, val value: String)
data class Feat348AnalyticsEvent7(val name: String, val value: String)
data class Feat348AnalyticsEvent8(val name: String, val value: String)
data class Feat348AnalyticsEvent9(val name: String, val value: String)
data class Feat348AnalyticsEvent10(val name: String, val value: String)

fun logFeat348Event1(event: Feat348AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat348Event2(event: Feat348AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat348Event3(event: Feat348AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat348Event4(event: Feat348AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat348Event5(event: Feat348AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat348Event6(event: Feat348AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat348Event7(event: Feat348AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat348Event8(event: Feat348AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat348Event9(event: Feat348AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat348Event10(event: Feat348AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat348Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat348Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat348(u: CoreUser): Feat348Projection1 =
    Feat348Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat348Projection1> {
    val list = java.util.ArrayList<Feat348Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat348(u)
    }
    return list
}
