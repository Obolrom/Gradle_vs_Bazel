package com.romix.feature.feat260

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat260Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat260UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat260FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat260UserSummary
)

data class Feat260UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat260NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat260Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat260Config = Feat260Config()
) {

    fun loadSnapshot(userId: Long): Feat260NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat260NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat260UserSummary {
        return Feat260UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat260FeedItem> {
        val result = java.util.ArrayList<Feat260FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat260FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat260UiMapper {

    fun mapToUi(model: List<Feat260FeedItem>): Feat260UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat260UiModel(
            header = UiText("Feat260 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat260UiModel =
        Feat260UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat260UiModel =
        Feat260UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat260UiModel =
        Feat260UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat260Service(
    private val repository: Feat260Repository,
    private val uiMapper: Feat260UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat260UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat260UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat260UserItem1(val user: CoreUser, val label: String)
data class Feat260UserItem2(val user: CoreUser, val label: String)
data class Feat260UserItem3(val user: CoreUser, val label: String)
data class Feat260UserItem4(val user: CoreUser, val label: String)
data class Feat260UserItem5(val user: CoreUser, val label: String)
data class Feat260UserItem6(val user: CoreUser, val label: String)
data class Feat260UserItem7(val user: CoreUser, val label: String)
data class Feat260UserItem8(val user: CoreUser, val label: String)
data class Feat260UserItem9(val user: CoreUser, val label: String)
data class Feat260UserItem10(val user: CoreUser, val label: String)

data class Feat260StateBlock1(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock2(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock3(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock4(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock5(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock6(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock7(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock8(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock9(val state: Feat260UiModel, val checksum: Int)
data class Feat260StateBlock10(val state: Feat260UiModel, val checksum: Int)

fun buildFeat260UserItem(user: CoreUser, index: Int): Feat260UserItem1 {
    return Feat260UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat260StateBlock(model: Feat260UiModel): Feat260StateBlock1 {
    return Feat260StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat260UserSummary> {
    val list = java.util.ArrayList<Feat260UserSummary>(users.size)
    for (user in users) {
        list += Feat260UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat260UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat260UiModel {
    val summaries = (0 until count).map {
        Feat260UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat260UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat260UiModel> {
    val models = java.util.ArrayList<Feat260UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat260AnalyticsEvent1(val name: String, val value: String)
data class Feat260AnalyticsEvent2(val name: String, val value: String)
data class Feat260AnalyticsEvent3(val name: String, val value: String)
data class Feat260AnalyticsEvent4(val name: String, val value: String)
data class Feat260AnalyticsEvent5(val name: String, val value: String)
data class Feat260AnalyticsEvent6(val name: String, val value: String)
data class Feat260AnalyticsEvent7(val name: String, val value: String)
data class Feat260AnalyticsEvent8(val name: String, val value: String)
data class Feat260AnalyticsEvent9(val name: String, val value: String)
data class Feat260AnalyticsEvent10(val name: String, val value: String)

fun logFeat260Event1(event: Feat260AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat260Event2(event: Feat260AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat260Event3(event: Feat260AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat260Event4(event: Feat260AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat260Event5(event: Feat260AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat260Event6(event: Feat260AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat260Event7(event: Feat260AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat260Event8(event: Feat260AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat260Event9(event: Feat260AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat260Event10(event: Feat260AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat260Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat260Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat260(u: CoreUser): Feat260Projection1 =
    Feat260Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat260Projection1> {
    val list = java.util.ArrayList<Feat260Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat260(u)
    }
    return list
}
